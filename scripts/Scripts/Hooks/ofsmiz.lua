package.path = package.path .. ";./LuaSocket/?.lua"
package.path = package.path .. ";./Scripts/?.lua"
package.cpath = package.cpath .. ";./LuaSocket/?.dll"

-- require external libs
local socket = require("socket")
local JSON = require("JSON")
-- local inspect = require("inspect")


-- declare hook
local ofsmiz = {}

-- Constants
local PORT = PORT or 3010
local POLL_PORT = POLL_PORT or 3011

local DATA_TIMEOUT_SEC = 0.001

-- Server and client connection handling
local client = nil
local server = nil

local poll_client = nil
local poll_server = nil

-- Internal flag
local do_step = false

-- Local testing config
local offline_testing = false  -- if running on local machine


-- Types of Handle
local HANDLE = {}
HANDLE.MESSAGE = "net_message"  -- maybe send a chat message?
HANDLE.ACTION = "net_action"  -- kick? or something
HANDLE.QUERY = "QUERY"  -- query player info?
HANDLE.EVENT = "net_event"
HANDLE.LOG = "net_log"  -- write to log
HANDLE.DEBUG = "DEBUG"
HANDLE.PULL = "net_pull"  -- for example, pull player entered slot
HANDLE.EXEC = "EXEC"  -- execute in mission env with no return
HANDLE.SUBMIT = "SUBMIT"  -- execute in mission en and returns res


-- Pull result table
local PULL = {}

PULL.wait_list = {}
PULL.result = {}  -- should be a key value pair: key is the uuid of the request, value is the calculated result
PULL.poll_result = {}

-- json rpc
local jsonrpc_parse_error		 = {code = -32700, message = "Parse error"}
local jsonrpc_invalid_request	 = {code = -32600, message = "Invalid Request"}
local jsonrpc_method_not_found	 = {code = -32601, message = "Method not found"}
local jsonrpc_invalid_params	 = {code = -32602, message = "Invalid params"}
local jsonrpc_internal_error	 = {code = -32603, message = "Internal error"}
local jsonrpc_server_error		 = {code = -32000, message = "Server error"}

-- jsonrpc, result, id
PULL.error_result = function(uuid, err)
	local json_response = {
		jsonrpc = "2.0",
		id = uuid,
		error = err
	}
	table.insert(PULL.result, json_response)
end

PULL.bake_result = function(uuid, processed_data)
	local json_response = {
		jsonrpc = "2.0",
		id = uuid,
		result = {
			data = processed_data,
			total = 1
		}
	}
	table.insert(PULL.result, json_response)
end

PULL.roast_result = function(uuid, processed_data, total_batch_num)
	local json_response = {
		jsonrpc = "2.0",
		id = uuid,
		result = {
			data = processed_data,
			total = total_batch_num
		}
	}
	table.insert(PULL.poll_result, json_response)
end

PULL.prepared_result = function() 
	local result = PULL.result
	PULL.result = {}
	return result
end

PULL.brew_result = function()
	local result = PULL.poll_result
	PULL.poll_result = {}
	return result
end


local function dostring_api_env(s)
	local f, err = loadstring(s)
	if f then
		return true, f()
	else
		return false, err
	end
end



--@Coroutine
--* iterate through net player list and get player info 
--* then add them to a table as key-value pairs
--@param none
--@yield none
--@return none
local function get_net_player_data(player_data_list, ...)
	local batch_result = {}
	local count = 0
	local batch_size = ... or 20
	local list_len = #player_data_list
	
	for i=1, list_len do
		local player_data = player_data_list[i] --> table
		table.insert(batch_result, player_data)
		
		count = count + 1
		if count == batch_size then
			local de = batch_result
			batch_result = {}                          	-- clean table
			count = 0                                  	-- reset count
			coroutine.yield(de)  			-- yield each batch in json
		end
	end
	
	if #batch_result < 1 then return nil
    else return batch_result end   -- return last batch
end



local function process_request(requests)  -- runs request from API Request
	for _,request in pairs(requests) do
		if request.method == HANDLE.QUERY then
			local player_data_list = {}
			local player_id_list = net.get_player_list() --> array of playerID --> when coroutine runs, player data might have been unavailable
			
			for i=1, #player_id_list do
				local player_data = net.get_player_info(player_id_list[i]) --> table
				table.insert(player_data_list, player_data)
			end
			
			local total_data_num = #player_data_list
			local batch_size = 4 -- let's say we have 64 players at most, 4 player each batch for a total of 16 batches
			local total_batch_num = math.ceil(total_data_num / batch_size)  --> if 1 player -> 1/4 -> 1, if 2 player -> 1
			
			-- net.log("before coroutine creation - " .. os.time())
			local co = coroutine.create(get_net_player_data)
			
			PULL.wait_list[request.id] = {
				["routine"] = co,
				["param"] = player_data_list,
				["collector"] = PULL.roast_result,
				["batch_size"] = batch_size,
				["total"] = total_data_num
			}  -- function get_net_player_data()

		elseif request.method == HANDLE.EXEC then
			local state = request.params[1]
			local lua_string = request.params[2]
			
			-- net.log("dostring in " .. state .. ": " .. lua_string)
			
			local res = net.dostring_in(state, lua_string)  --> string only
			
			-- 'server': holds the current mission when multiplayer? server only
			-- 'config': the state in which $INSTALL_DIR/Config/main.cfg is executed, as well as $WRITE_DIR/Config/autoexec.cfg
			--           used for configuration settings
			-- 'mission': holds current mission
			-- 'export': runs $WRITE_DIR/Scripts/Export.lua and the relevant export API
			
			PULL.bake_result(request.id, res, 0)
			
		else  -- no matching handle, generate a rpc response with error code and message
		
		
		end
	end
	
	
end


local coroutine_flip = 0
local function manage_coroutine()
	for uuid, co_data in pairs(PULL.wait_list) do
		local co = co_data["routine"]
		local param = co_data["param"]
		local collector = co_data["collector"]  -- function
		local batch_size = co_data["batch_size"] or 1
		local total = co_data["total"]
		
		local status = coroutine.status(co)
		if status == 'dead' then
			-- net.log(uuid .. "...coroutine completed")
			
			-- if the coroutine never starts because param is empty, create an empty report
			if total == 0 then
				collector(uuid, {}, total)
			end
			
			
			PULL.wait_list[uuid] = nil  -- remove coroutine from the table
			-- PULL.bake_result(uuid, result)
			
		else  -- if coroutine is not dead
			
			coroutine_flip = coroutine_flip + 1
			if coroutine_flip == 5 then
				coroutine_flip = 0
			
				local _, res = coroutine.resume(co, param, batch_size)  -- resume coroutine and get yield or return result
				if res then  -- if return / yield is not nil
					
					-- net.log("batch!")
					
					--[[ @TEST: print name of each group
					for k,v in pairs(res) do
						trigger.action.outText(v.name, 1)
					end
					--]]
					
					collector(uuid, res, total)

					-- @TEST return partial result
					-- PULL.wait_list[uuid] = nil
				end
			end
		
			
		end
		
	end
end


local function step()
	if server then
		server:settimeout(0)  -- give up if no connection
		client = server:accept()  -- accept client
		
		if client then  -- if client not nil, connection established
			--client:settimeout(0.001)
			local line, err = client:receive()
			if not err then
				-- net.log(line)
				local success, requests = pcall(
					function()
						return JSON:decode(line)
					end
				)
				if success then  -- run request here

					local readyResult = PULL.prepared_result()
					local bytes, status, lastbyte = client:send(JSON:encode(readyResult) .. '\n')
					
					-- run_request
					process_request(requests)
				else
					-- log(requests)  -- log error
					local bytes, status, lastbyte = client:send(JSON:encode(false) .. '\n')
				end
			else
				net.log(tostring(err))
			end
			
			client:close()  -- done, close connection
			client = nil
		end
	end
end

local function poll_step()
	if poll_server then
		poll_server:settimeout(0)  -- give up if no connection
		poll_client = poll_server:accept()  -- accept poll_client
		
		if poll_client then  -- if poll_client not nil, connection established
			local line, err = poll_client:receive()
			
			if not err then
				-- net.log(line)
				-- env.info("poll step: " .. line)
				local success, requests = pcall(
					function()
						return JSON:decode(line)
					end
				)
				if success then  -- run request here
					local readyResult = PULL.brew_result()  -- send nothing back to polling thread
					-- local readyResult = {}
					local bytes, status, lastbyte = poll_client:send(JSON:encode(readyResult) .. '\n')
					
					-- run_request
					process_request(requests)
				else
					env.info(requests)  -- log error
					local readyResult = PULL.brew_result()
					local bytes, status, lastbyte = poll_client:send(JSON:encode(readyResult) .. '\n')
				end
			else
				net.log(tostring(err))
			end
			
			poll_client:close()  -- done, close connection
			poll_client = nil
		end
	end
end



-----------------------------------------------------------------------------------------

function ofsmiz.onSimulationStart()
	log("Starting DCS API CONTROL server")
	
	server = assert(socket.bind("127.0.0.1", PORT))
	poll_server = assert(socket.bind("127.0.0.1", POLL_PORT))
	-- server:settimeout(0.001)
	local ip, port = server:getsockname()
	local poll_ip, poll_port = poll_server:getsockname()
	net.log("DCS API Server: Started on Port " .. port .. " at " .. ip)
	net.log("DCS API Poll Server: Started on Port " .. poll_port .. " at " .. poll_ip)
end


local total_steps = 0
local step_frame_count = 0
function ofsmiz.onSimulationFrame()
	if do_step then
		step_frame_count = step_frame_count + 1
		if step_frame_count == 1 then -- at every 10th frame
			--net.log("this is frame: " .. step_frame_count .. " at " .. os.time())
		
			local success, err = pcall(step)
			local poll_success, poll_err = pcall(poll_step)
			manage_coroutine()
			
			step_frame_count = 0
		end
	end
end


function ofsmiz.onSimulationStop()
	server:close()
	net.log("API CONTROL SERVER TERMINATED")
end

function ofsmiz.onNetConnect(localPlayerID)  -- only if isServer()
	-- this is where the netview is initiated?

	net.log("onNetConnect")
	-- map slot and id and things
	NetSlotInfo = {}  -- reset NetSlotInfo
	-- local coals = DCS.getAvailableCoalitions() --> table { [coalition_id] = { name = "coalition name", } ... }
	local blue_slots = DCS.getAvailableSlots("blue")
	local red_slots = DCS.getAvailableSlots("red")
	-- not sure about red slots, ignore atm
	
	for slot_id, slot_info in ipairs(blue_slots) do
		NetSlotInfo[slot_id] = {
			["action"] 		= slot_info.action,
			["countryName"] = slot_info.countryName,
			["groupName"] 	= slot_info.groupName,
			["groupSize"] 	= slot_info.groupSize,
			["onboard_num"] = slot_info.onboard_num,
			["role"] 		= slot_info.role,
			["type"] 		= slot_info.type,
			["task"] 		= slot_info.task,
			["unitId"] 		= slot_info.unitId,
		}
	end
	
	--[[
	[15] = {
	action = "From Ground Area",
	callsign = { 1, 1, 1, name = "Enfield11"},
	countryName = "USA",
	groupName = "F-5E-3 (02) Sn: 761527",
	groupSize = 1,
	onboard_num = "04",
	role = "pilot",
	task = "CAP",
	type = "F-5E-3",
	unitId = "760"
}
	--]]
	
	do_step = true -- onSimulationFrame can start step()
end


-- function ofsmiz.onPlayerChangeSlot(id)
	
-- end

function ofsmiz.onNetDisconnect(reason_msg, err_code)
	net.log("onNetDisconnect")
	do_step = false -- onSimulationFrame can start step()
	
	PULL.wait_list = {}
	PULL.result = {}  -- should be a key value pair: key is the uuid of the request, value is the calculated result
	PULL.poll_result = {}
end

---[[
function ofsmiz.onPlayerTryConnect(addr, name, ucid, playerID)  -- check if on black list?
	if true then  -- if player is not on blacklist
		-- do I have info on this ucid? check for ucid key
		net.log("passed blacklist check")
		local new_conn_info = {}
		new_conn_info.ipaddr = addr
		new_conn_info.name = name
		new_conn_info.ucid = ucid
		new_conn_info.playerID = playerID
		new_conn_info.lang = net.get_player_info(playerID).lang
		
		-- check if user data exists?
		NetPlayerInfo[ucid] = new_conn_info  -- key is ucid
		NetPlayerInfoById[playerID] = new_conn_info  -- key is player id in the net env
		--]]
		net.log("New Connection: " .. new_conn_info.ipaddr .. ", name: " .. new_conn_info.name .. ", ucid: " .. new_conn_info.ucid .. ", lang: " .. new_conn_info.lang)
		return true
	else
		return false, "Banned"
	end
end


function ofsmiz.onPlayerTryChangeSlot(playerID, side, slotID) -- -> true | false
	net.log("onPlayerTryChangeSlot")
	
	-- assuming the id is actually the slot_id
	local player_info = net.get_player_info(playerID) --> client language can be get here
	
	
	-- for k, v in pairs(player_info) do
		-- net.log(k .. " -> " .. v)
	-- end
	
	NetPlayerInfoById[playerID].slotID = slotID
	
	local p = {}
	p.slotID_TO = slotID
	p.slotID_FROM = player_info.slot
	p.slotID_player = player_info.name
	
	local rt = {
		PULL.SLOT_CHANGE,
		p,
	}
	bake_pull(rt)
	
	
	--NetPlayerInfo[player_info.id] = player_info
	--net.log(player_info.id)
	--net.log(player_info.name)
	--net.log(player_info.side)  -- side before changing slot, 0 is neutral
	--net.log(player_info.slot)  -- slot number before changing, "" is observer list
	--net.log(player_info.ucid)
	--net.log(player_info.ipaddr)  -- doesn't return localhost ip for local player (server)
	net.log("End onPlayerTryChangeSlot")
	
    -- return true
end

-- TODO: update this table when backend starts or on need
local chat_cmd_syntax = {
	'/info', '/信息', '/xinxi', '/chaxun', '/查询',  -- information query related
	
	'/roll',  -- roll dice
	
	'/ll', '/latlon', '/经纬',  -- coord conversion: mgrs to ll
	'/mgrs', '/utm', '/战术', '战术坐标',  -- coord conversion: convert ll to mgrs
	'/lo', '/xy', '/vec3', -- coord conversion: ll to Lo

	
	'/bra', '/dir',  -- direction
	
	'/bullseye', '/bulls', -- to bulls coord
	
	'/meter', '/meters', '/米', '/m',  -- unit conversion: to meter
	'/ft', '/feet', '/foot', '/英尺',  -- unit conversion: to meter
	'/nm', '/mile', '/miles', '/海里', -- unit conversion: to nautical mile
	'/km', '/kilometer', '/kilometers', '/千米',  -- unit conversion: to kilometers
	'/kt', '/kts', '/knot', '/knots', '/节',  -- unit conversion: to knots
	'/kmh', '/kmph', '/千米时', '/千米/时',  -- unit conversion: to kilometers per hour
	
	'/kg', '/kgs', '/kilogram', '/kilograms', '/千克', '/公斤',  -- unit conversion: to kilograms
	'/lb', '/lbs', '/pound', '/pounds', '/磅', '/英镑',  -- unit conversion: to pounds
	
	'/help', '/帮助', '/cmd', '/commands', '/cmds',  -- help message
	
	'/admin',  -- admin options
	
	'/debug',  -- debug options
}

function ofsmiz.onPlayerTrySendChat(playerID, msg, all) -- -> filteredMessage | "" - empty string drops the message
    -- check if this message is a valid chat command
	-- that means, if it has a leading keyword in the in command list
	-- if it is has such keyword, bake command and send to python, else return the orginal msg
	
	-- somehow the local server chat message is also catched
	
	local words = {}
	for word in msg:gmatch("%S+") do words[#words + 1] = word end
	-- check list contain the inital word
	local check_word = string.lower(words[1])
	if has_value(chat_cmd_syntax, check_word) then  -- contains 
		-- bake pull here, return "" to drop the message
		local p = {}
		p.check_word = check_word
		p.msg = msg
		p.player_id = playerID
		
		local rt = {
			PULL.CHAT_CMD,
			p,
		}
		bake_pull(rt)
		
		return ""
	else  -- no match, regular msg, return
		return msg
	end
	
	return msg
end




--]]

net.log("Loading OFSMIZ interface")

DCS.setUserCallbacks(ofsmiz)  -- here we set our callbacks