package.path = package.path .. ";./LuaSocket/?.lua"
package.path = package.path .. ";./Scripts/?.lua"
package.cpath = package.cpath .. ";./LuaSocket/?.dll"

local LAVA = {}

-- require external libs
local socket = require("socket")
local JSON = require("JSON")

-- Constants
local PORT = PORT or %d
local POLL_PORT = POLL_PORT or %d

local DATA_TIMEOUT_SEC = 0.001

local last_comm_timestamp
local last_poll_timestamp

-- File Ouput
local default_output_file = nil

-- Server and client connection handling
local client = nil
local server = nil

local poll_client = nil
local poll_server = nil

-- Types of Handle
local HANDLE = {}
HANDLE.QUERY = "QUERY"
HANDLE.DEBUG = "DEBUG"
HANDLE.EXEC = "EXEC"
HANDLE.RESET = "RESET"

-- Flags
local coroutine_flip = 0

-- JSON RPC (WIP)
local jsonrpc_parse_error		 = {code = -32700, message = "Parse error"}
local jsonrpc_invalid_request	 = {code = -32600, message = "Invalid Request"}
local jsonrpc_method_not_found	 = {code = -32601, message = "Method not found"}
local jsonrpc_invalid_params	 = {code = -32602, message = "Invalid params"}
local jsonrpc_internal_error	 = {code = -32603, message = "Internal error"}
local jsonrpc_server_error		 = {code = -32000, message = "Server error"}

-- Prev Export functions
local _prevExport = {}
_prevExport.LuaExportActivityNextEvent = LuaExportActivityNextEvent
_prevExport.LuaExportBeforeNextFrame = LuaExportBeforeNextFrame
_prevExport.LuaExportStart = LuaExportStart
_prevExport.LuaExportStop = LuaExportStop



local previous_export_data = {}

local next = next



local PULL = {}
PULL.wait_list = {}
PULL.result = {}  -- should be a key value pair: key is the uuid of the request, value is the calculated result
PULL.poll_result = {}

local function initData()
	coroutine_flip = 0
	PULL.wait_list = {}
	PULL.result = {}
	PULL.poll_result = {}
	previous_export_data = {}
end

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


PULL.roast_result = function(uuid, processed_data, tail)
	if tail then  -- if it is tail, then can send empty response with tail tag
		local json_response = {
			jsonrpc = "2.0",
			id = uuid,
			result = {
				data = processed_data,
				is_tail = tail
			}
		}
		table.insert(PULL.poll_result, json_response)
		return
	end

	if processed_data and next(processed_data) ~= nil then  -- otherwise requires not nil and not empty
		local json_response = {
			jsonrpc = "2.0",
			id = uuid,
			result = {
				data = processed_data,
				is_tail = tail
			}
		}
		table.insert(PULL.poll_result, json_response)
	end

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





-- dostring in Export
local function dostring_export_env(s)
	local f, err = loadstring(s)
	if f then
		return true, f()
	else
		return false, err
	end
end

local function TableConcat(t1,t2)
    for i=1,#t2 do
        t1[#t1+1] = t2[i]
    end
    return t1
end

local function table_combine(t1, t2)
	for key, value in pairs(t2) do
		t1[key] = value
	end
	return t1
end

local function has_value(tab, val)
    for index, value in ipairs(tab) do
        if value == val then
            return true
        end
    end

    return false
end


local attributes_ignores = {
	"Coalition", "CoalitionID", "Country", "GroupName", "Name", "UnitName"
}

local subkey_ignores = {
	"Static", "Born", "level1", "level2", "level3", "level4"
}

local function get_delta_export_data(current_data, ...)

	local previous_data = previous_export_data

	local batch_delta = {}  -- list of data, and each contains a tag of create, update or delete
	local count = 0
	local batch_size = ... or 20


	-- we can ignore static object update because they are unlikely to move
	-- and if they are move then they must be despawned and respawn with a different runtime id
	-- whether an object is static can be determined by accessing Flags.Static
	for runtime_id, object_data in pairs(current_data) do
		if previous_data[runtime_id] then  -- if previous data has this runtime id --> update data
			-- we can ignore static object update because they are unlikely to move
			-- and if they are move then they must be despawned and respawn with a different runtime id
			-- whether an object is static can be determined by accessing Flags.Static

			-- also, some fields are highly unlikely to change, so we can ignore that as well

			--[[
			[16777473] = {											can change?
				Bank = 0,											true
				Coalition = "Allies",								false
				CoalitionID = 1,									false
				Country = 7,										false
				Flags = {
				  AI_ON = true,										true
				  Born = true,										true? not sure, if it is not born then it should not even be in the export data
				  Human = false,									true? not sure, if you enter a combined arms unit then maybe it will change?
				  IRJamming = false,								true
				  Invisible = false,								true
				  Jamming = false,									true
				  RadarActive = false,								true
				  Static = true										false
				},
				GroupName = "ATIS MCCARRAN INTERNATIONAL",			false
				Heading = 6.2831854820251,							true
				LatLongAlt = {
				  Alt = 1654.9458007813,							true
				  Lat = 37.401074210626,							true
				  Long = -116.23826415644							true
				},
				Name = "f_bar_cargo",								false
				Pitch = 0,											true
				Position = {
				  x = -270389.5,									true
				  y = 1654.9458007813,								true
				  z = -126577.671875								true
				},
				Type = {
				  level1 = 0,										false
				  level2 = 0,										false
				  level3 = 0,										false
				  level4 = 0										false
				},
				UnitName = "ATIS MCCARRAN INTERNATIONAL"			false
			}
			--]]

			if not current_data[runtime_id].Flags.Static then  -- check for update only if this data is not flagged as static object
				for attribute_name, attribute_value in pairs(object_data) do  -- for each key value pairs
					if type(attribute_value) ~= 'table' then  -- if value is not a table
						if not has_value(attributes_ignores, attribute_name) then  -- if attribute_name not to be ignored
							if previous_data[runtime_id][attribute_name] ~= attribute_value then  -- if value has changed, put it to delta
								if type(attribute_value) == 'number' then  -- if value is number, we can compare its value to the previous value
									if previous_data[runtime_id][attribute_name] then  -- if previous value exists --> is this check necessary?
										local value_delta = math.abs(previous_data[runtime_id][attribute_name] - attribute_value)  -- calculate delta

										if value_delta > 0.001 then  -- publish change only if delta is larger than 0.001 to avoid frequent update by some weird object position jingling
											log("publish change because " .. value_delta .. " > 0.001")
											-- change
											batch_delta[runtime_id] = batch_delta[runtime_id] or {}  -- new table if none
											batch_delta[runtime_id]["data"] = batch_delta[runtime_id]["data"] or {}
											batch_delta[runtime_id]["data"][attribute_name] = attribute_value

											if not batch_delta[runtime_id]["action"] then  -- set action if it has not been set
												batch_delta[runtime_id]["action"] = "update"
											end
										end
									else  -- no data to compare, publish new
										-- change
										batch_delta[runtime_id] = batch_delta[runtime_id] or {}  -- new table if none
										batch_delta[runtime_id]["data"] = batch_delta[runtime_id]["data"] or {}
										batch_delta[runtime_id]["data"][attribute_name] = attribute_value

										if not batch_delta[runtime_id]["action"] then  -- set action if it has not been set
											batch_delta[runtime_id]["action"] = "update"
										end
									end
								else  -- not a number, if value not equal then it is changed
									batch_delta[runtime_id] = batch_delta[runtime_id] or {}  -- new table if none
									batch_delta[runtime_id]["data"] = batch_delta[runtime_id]["data"] or {}
									batch_delta[runtime_id]["data"][attribute_name] = attribute_value

									if not batch_delta[runtime_id]["action"] then  -- set action if it has not been set
										batch_delta[runtime_id]["action"] = "update"
									end
								end
							end
						end
					else -- value is indeed a table, parse directly for simplicity since all table has only one level at most
						local new_sub_data = {}
						for subkey, subvalue in pairs(attribute_value) do
							if not has_value(subkey_ignores, subkey) then  -- if subkey not to be ignored
								if previous_data[runtime_id][attribute_name][subkey] ~= subvalue then
									-- if the value is a number, then if the difference is less than 0.0001, we should ignore the change
									if type(subvalue) == 'number' then
										if math.abs(previous_data[runtime_id][attribute_name][subkey] - subvalue) > 0.001 or subkey == 'Alt' or subkey == 'Lat' or subkey == 'Long' then
											-- change
											new_sub_data[subkey] = subvalue

											batch_delta[runtime_id] = batch_delta[runtime_id] or {}  -- new table if none
											batch_delta[runtime_id]["data"] = batch_delta[runtime_id]["data"] or {}
											batch_delta[runtime_id]["data"][attribute_name] = attribute_value
											if not batch_delta[runtime_id]["action"] then  -- set action if it has not been set
												batch_delta[runtime_id]["action"] = "update"
											end
										end
									else  -- not a number, if value not equal then it is changed
										new_sub_data[subkey] = subvalue

										batch_delta[runtime_id] = batch_delta[runtime_id] or {}  -- new table if none
										batch_delta[runtime_id]["data"] = batch_delta[runtime_id]["data"] or {}
										batch_delta[runtime_id]["data"][attribute_name] = attribute_value
										if not batch_delta[runtime_id]["action"] then  -- set action if it has not been set
											batch_delta[runtime_id]["action"] = "update"
										end
									end
								end
							end
						end

						if next(new_sub_data) ~= nil then  -- if there is actually a change in table value
							batch_delta[runtime_id] = batch_delta[runtime_id] or {}  -- new table if none
							batch_delta[runtime_id][attribute_name] = new_sub_data
						end
					end
				end
			end

		else  -- if previous data does not contain this runtime id --> create data
			batch_delta[runtime_id] = {
				data = object_data,
				action = "create"
			}
		end

		-- split batch
		count = count + 1
		if count == batch_size then
			if batch_data == {} then  -- no create or update at all
				coroutine.yield(nil, false)
			else  -- at least has one change
				local de = {}
				-- map to list and add runtime_id
				for runtime_id, diff in pairs(batch_delta) do
					diff["data"]["RuntimeID"] = runtime_id

					table.insert(de, diff)
				end

				batch_delta = {}
				count = 0
				coroutine.yield(de, false)
			end
		end
	end

	-- this part doesn't seem to need any coroutine because it is relatively light
	for runtime_id, object_data in pairs(previous_data) do
		if not current_data[runtime_id] then
			batch_delta[runtime_id] = {
				action = "delete",
				data = {
					["RuntimeID"] = runtime_id
				}
			}
		end
	end

	previous_export_data = current_data  -- keep a copy of current_export_data

	local batch_delta_size = 0
	for _,_ in pairs(batch_delta) do  -- count batch size
		batch_delta_size = batch_delta_size + 1
	end

	if batch_delta_size < 1 then
		return nil, true
	else
		local de = {}
		-- map to list and add runtime_id
		for runtime_id, diff in pairs(batch_delta) do
			diff["data"]["RuntimeID"] = runtime_id

			table.insert(de, diff)
		end

		return de, true
	end   -- return last batch
end


local function process_request(requests)  -- runs request from Export Request
	for _, request in pairs(requests) do
		if request.method == HANDLE.QUERY then
			local all_objects = LoGetWorldObjects("units")
			local all_ballistics = LoGetWorldObjects("ballistic")

			local current_export_data = table_combine(all_objects, all_ballistics)

			local data_size = 0
			-- get size of key value pair table
			for _,_ in pairs(current_export_data) do
				data_size = data_size + 1
			end

			-- prepare coroutine
			local total_data_num = data_size
			-- local batch_size = math.ceil(total_data_num * DATA_TIMEOUT_SEC * 10) -- 10  -- num count of all data divided by 100 (each step)
			local batch_size = 10
			local total_batch_num = math.ceil(total_data_num / batch_size)

			local co = coroutine.create(get_delta_export_data)

			PULL.wait_list[request.id] = {
				["routine"] = co,
				["param"] = current_export_data,
				["collector"] = PULL.roast_result,
				["batch_size"] = batch_size,
				["total"] = total_data_num
			}  -- function get_delta_export_data()

		elseif request.method == HANDLE.EXEC then
			local lua_string = request.params[1]

			local success, returned, res = pcall(dostring_export_env, lua_string)

			PULL.bake_result(request.id, res, 0)

		elseif request.method == HANDLE.RESET then
			-- reset diff comp table
			initData()
			log("data reset complete")
		end

	end
end



local function step()
	if server then
		if not client then  -- if no client, establish connection from server object
			client = server:accept()
      
			if client then
				client:settimeout(0)
				-- try to receive from client?
				local line, err = client:receive()
			
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
						if requests then
							process_request(requests)
						end
					else
						log("Request: " .. requests)  -- log error
						local bytes, status, lastbyte = client:send(JSON:encode(false) .. '\n')
					end
					--]]
				else
					log(line, err)
					client:send("\r\n")
				end
			end
      
		else  -- existing client, reuse this object
			-- reuse client
			local line, err = client:receive()
      
			if err then
				if err == 'timeout' then
					if last_comm_timestamp then  
						if os.time() - last_comm_timestamp > 5 then
							log("connection is lost")
							
							log("current os.time is " .. os.time() .. " and last handshake timestamp is " .. last_comm_timestamp)
							log("diff is " .. os.time() - last_comm_timestamp)
					
							client:close()
							client = nil
							last_comm_timestamp = nil
							return
						end
					else  -- no successful handshake is made yet
						log("no handshake")
					end
			  
				elseif err == 'closed' then
					log("connection is closed")
					client:close()
					client = nil
					last_comm_timestamp = nil
					return
			  
				else  -- other error
					log("Error: " .. err)
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
					if requests then
						process_request(requests)
					end
				else
					log("Request: " .. requests)  -- log error
					local bytes, status, lastbyte = client:send(JSON:encode(false) .. '\n')
				end
				--]]
			end
		end
	end
end

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
						local readyResult = PULL.brew_result()  -- send nothing back to polling thread
						local bytes, status, lastbyte = poll_client:send(JSON:encode(readyResult) .. '\n')

						if requests then
							process_request(requests)
						end
					else
						env.info(requests)  -- log error
						local readyResult = PULL.brew_result()
						local bytes, status, lastbyte = poll_client:send(JSON:encode(readyResult) .. '\n')
					end
					--]]
				else
					log(line, err)
					poll_client:send("\r\n")
				end
			end
      
		else  -- existing client, reuse this object
			-- reuse client
			local line, err = poll_client:receive()
      
			if err then
				if err == 'timeout' then
					if last_poll_timestamp then  
						if os.time() - last_poll_timestamp > 5 then
							
							log("current os.time is " .. os.time() .. " and last handshake timestamp is " .. last_poll_timestamp)
							log("diff is " .. os.time() - last_poll_timestamp)
							
							log("connection is lost")
					
							poll_client:close()
							poll_client = nil
							last_poll_timestamp = nil
							return
						end
					else  -- no successful handshake is made yet
						log("no handshake")
					end
					
				elseif err == 'closed' then
					log("connection is closed")
					poll_client:close()
					poll_client = nil
					last_poll_timestamp = nil
					return
			  
				else  -- other error
					log("Error: " .. err)
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
					local readyResult = PULL.brew_result()  -- send nothing back to polling thread
					local bytes, status, lastbyte = poll_client:send(JSON:encode(readyResult) .. '\n')

					if requests then
						process_request(requests)
					end
				else
					env.info(requests)  -- log error
					local readyResult = PULL.brew_result()
					local bytes, status, lastbyte = poll_client:send(JSON:encode(readyResult) .. '\n')
				end
				--]]
			end
		end
	end
end


local function parse_export_data(runtime_id, object_data) 
	-- insert runtime id into object_data and add to collection
	object_data["RuntimeID"] = runtime_id
	
	return object_data
end  -- parse_export_data()


-- add key value pair into into result
local function get_export_object_data(dict_objects, ...)  -- key value pairs
	local batch_result = {}
    local count = 0
    local batch_size = ... or 20					-- default batch size is 20
    
	for runtime_id_num, object_data in pairs(dict_objects) do
		local runtime_id_tagged_object_data = parse_export_data(runtime_id_num, object_data)
		table.insert(batch_result, runtime_id_tagged_object_data)
		
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










local function manage_coroutine()
	for uuid, co_data in pairs(PULL.wait_list) do
		local co = co_data["routine"]
		local param = co_data["param"]
		local collector = co_data["collector"]  -- function
		local batch_size = co_data["batch_size"] or 1
		local total = co_data["total"]
		
		local status = coroutine.status(co)
		if status == 'dead' then
			PULL.wait_list[uuid] = nil  -- remove coroutine from the table
		else  -- if coroutine is not dead
			
			coroutine_flip = coroutine_flip + 1
			if coroutine_flip == 1 then
				coroutine_flip = 0
			
				local _, res, tail = coroutine.resume(co, param, batch_size)  -- resume coroutine and get yield or return result
				
				if tail then
					collector(uuid, res, tail)
				else
					if res and type(res) == 'string' then  -- error
						log("Coroutine Error: " .. res)
					end
					
					if res and next(res) ~= nil then
						collector(uuid, res, tail)
					end
				end
			end
		end
		
	end
end

function LuaExportStart()
	log("Starting DCS Export Server")
	
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
	
	log("DCS Export Request Server: Started on Port " .. port .. " at " .. ip)
	log("DCS Export Poll Server: Started on Port " .. poll_port .. " at " .. poll_ip)
	
	initData()
end

function LuaExportStop()
	if default_output_file then
		default_output_file:close()
		default_output_file = nil
	end

	if client then client:close() end
    if server then server:close() end
	  
    if poll_client then poll_client:close() end
    if poll_server then poll_server:close() end
	
	log("DCS Export Server Terminated")
end

local srs_cyclic_counter = 0
function LuaExportActivityNextEvent(t)
	local tNext = t
	tNext = tNext + DATA_TIMEOUT_SEC
	
	step()
	poll_step()
	
	manage_coroutine()
	
	-- run previous export activity to support SimpleRadioStandalone, which queries every 0.2 second
	-- our base f is 0.001 second, so we can flip a variable
	srs_cyclic_counter = srs_cyclic_counter + 1
	if srs_cyclic_counter == 200 then
		local _status, _result = pcall(function() 
			if _prevExport.LuaExportActivityNextEvent then
				_prevExport.LuaExportActivityNextEvent(t)
			end
		end)
		
		if not _status then
			log('ERROR Calling other LuaExportActivityNextEvent from another script: ' .. _result)
		end
		
		srs_cyclic_counter = 0
	end

	return tNext
end