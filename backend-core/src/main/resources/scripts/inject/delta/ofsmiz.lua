package.path = package.path .. ";./LuaSocket/?.lua"
package.path = package.path .. ";./Scripts/?.lua"
package.cpath = package.cpath .. ";./LuaSocket/?.dll"

-- require external libs
local socket = require("socket")
local JSON = require("JSON")

-- declare hook
local ofsmiz = {}

-- Constants
local PORT = PORT or %d
local POLL_PORT = POLL_PORT or %d

local last_comm_timestamp
local last_poll_timestamp

local DATA_TIMEOUT_SEC = 0.001

-- Server and client connection handling
local client = nil
local server = nil

local poll_client = nil
local poll_server = nil

local react_client = nil
local react_server = nil

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
PULL.react_result = {}

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

PULL.grind_result = function(uuid, processed_data, total_batch_num)
	local json_response = {
		jsonrpc = "2.0",
		id = uuid,
		result = {
			data = processed_data,
			total = total_batch_num
		}
	}
	table.insert(PULL.react_result, json_response)
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

PULL.mush_result = function()
	local result = PULL.react_result
	PULL.react_result = {}
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
			local success, res
			if state == 'debug' then
				success, res = dostring_api_env(lua_string)
			else
				res = net.dostring_in(state, lua_string)  --> string only
			end
			
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
			if total == 0 then
				collector(uuid, {}, total)
			end
			
			PULL.wait_list[uuid] = nil  -- remove coroutine from the table
			
		else  -- if coroutine is not dead
			
			coroutine_flip = coroutine_flip + 1
			if coroutine_flip == 5 then
				coroutine_flip = 0
			
				local _, res = coroutine.resume(co, param, batch_size)  -- resume coroutine and get yield or return result
				if res then  -- if return / yield is not nil
					collector(uuid, res, total)
				end
			end
		
			
		end
		
	end
end


local function step()
	if server then
		if not client then  -- if no client, establish connection from server object
			client = server:accept()
      
			if client then
				client:settimeout(0)
				
				local line, err = client:receive()  -- should immediately receive a line from client otherwise assert fail
			
				if not err then
					last_comm_timestamp = os.time()  -- update on successful handshake

					---[[
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
						net.log("Requests: " .. requests)  -- log error
						local bytes, status, lastbyte = client:send(JSON:encode(jsonrpc_parse_error) .. '\n')
					end
					--]]
				else
					net.log(line, err)
					client:send(JSON:encode(jsonrpc_server_error) + "\r\n")
				end
			end
      
		else  -- existing client, reuse this object
			-- reuse client
			local line, err = client:receive()
      
			if err then
				if err == 'timeout' then
					if os.time() - last_comm_timestamp > 5 then
						net.log("connection is lost")
				
						client:close()
						client = nil
						last_comm_timestamp = nil
						return
					end

				elseif err == 'closed' then
					net.log("connection is closed")
					client:close()
					client = nil
					last_comm_timestamp = nil
					return
			  
				else  -- other error
					net.log(err)
				end
			end

			if not err then  -- successful handshake
				last_comm_timestamp = os.time()
			
				---[[
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
					net.log(requests)  -- log error
					local bytes, status, lastbyte = client:send(JSON:encode(jsonrpc_parse_error) .. '\n')
				end
				--]]
			end
		end
	end
end

---[[
local function poll_step()
	if poll_server then
		if not poll_client then  -- if no client, establish connection from server object
			poll_client = poll_server:accept()
      
			if poll_client then
				poll_client:settimeout(0)
				-- try to receive from client?
				local line, err = poll_client:receive()
			
				if not err then
					last_poll_timestamp = os.time()  -- update on successful handshake

					---[[
					local success, requests = pcall(
						function()
							return JSON:decode(line)
						end
					)
					if success then  -- run request here
						local readyResult = PULL.brew_result()
						local bytes, status, lastbyte = poll_client:send(JSON:encode(readyResult) .. '\n')
						
						-- run_request
						process_request(requests)
					else
						net.log(requests)  -- log error
						local bytes, status, lastbyte = poll_client:send(JSON:encode(jsonrpc_parse_error) .. '\n')
					end
					--]]
				else
					net.log(line, err)
					client:send(JSON:encode(jsonrpc_server_error) + "\r\n")
				end
			end
      
		else  -- existing client, reuse this object
			-- reuse client
			local line, err = poll_client:receive()
      
			if err then
				if err == 'timeout' then

					if os.time() - last_poll_timestamp > 5 then
						net.log("connection is lost")
				
						poll_client:close()
						poll_client = nil
						last_poll_timestamp = nil
						return
					end

				elseif err == 'closed' then
					net.log("connection is closed")
					poll_client:close()
					poll_client = nil
					last_poll_timestamp = nil
					return
			  
				else  -- other error
					net.log(err)
				end
			end

			if not err then  -- successful handshake
				last_poll_timestamp = os.time()
			
				---[[
				local success, requests = pcall(
					function()
						return JSON:decode(line)
					end
				)
				if success then  -- run request here
					local readyResult = PULL.brew_result()
					local bytes, status, lastbyte = poll_client:send(JSON:encode(readyResult) .. '\n')
					
					-- run_request
					process_request(requests)
				else
					net.log(requests)  -- log error
					local bytes, status, lastbyte = poll_client:send(JSON:encode(false) .. '\n')
				end
				--]]
			end
		end
	end
end
--]]

function ofsmiz.onSimulationStart()
	-- Request Server
	server = socket.tcp()
	
	assert(server:bind("localhost", PORT))
	assert(server:listen())
	
	server:settimeout(0)  -- non-blocking
	server:setoption("reuseaddr", true)
	server:setoption("keepalive", true)
	server:setoption("tcp-nodelay", true)

	-- Poll Server
	poll_server = socket.tcp()
	
	assert(poll_server:bind("localhost", POLL_PORT))
	assert(poll_server:listen())
	
	poll_server:settimeout(0)  -- non-blocking
	poll_server:setoption("reuseaddr", true)
	poll_server:setoption("keepalive", true)
	poll_server:setoption("tcp-nodelay", true)
	
	-- write log
	local ip, port = server:getsockname()
	local poll_ip, poll_port = poll_server:getsockname()
	
	net.log("DCS API Request Server: Started on Port " .. port .. " at " .. ip)
	net.log("DCS API Poll Server: Started on Port " .. poll_port .. " at " .. poll_ip)
end


local total_steps = 0
local step_frame_count = 0
function ofsmiz.onSimulationFrame()
	if do_step then
		step_frame_count = step_frame_count + 1
		if step_frame_count == 1 then -- at every 10th frame
			-- net.log("this is frame: " .. step_frame_count .. " at " .. os.time())
		
			local success, err = pcall(step)
			local poll_success, poll_err = pcall(poll_step)
			
			manage_coroutine()
			
			step_frame_count = 0
		end
	end
end


function ofsmiz.onSimulationStop()
	do_step = false
	
    if client then client:close() end
    if server then server:close() end
	  
    if poll_client then poll_client:close() end
    if poll_server then poll_server:close() end
  
	PULL.wait_list = {}
	PULL.result = {}
	PULL.poll_result = {}
	PULL.react_result = {}
	
	net.log("onSimulationStop -> API CONTROL SERVER TERMINATED")
end

function ofsmiz.onSimulationResume()
	net.log("onSimulationResume -> start do step")
	do_step = true
end


net.log("Loading OFSMIZ interface")

DCS.setUserCallbacks(ofsmiz)  -- here we set our callbacks