package.path = package.path .. ";./LuaSocket/?.lua"
package.path = package.path .. ";./Scripts/?.lua"
package.cpath = package.cpath .. ";./LuaSocket/?.dll"

local LAVA = {}

-- require external libs
local socket = require("socket")
local JSON = require("JSON")

-- Constants
local PORT = PORT or 3012
local POLL_PORT = POLL_PORT or 3013

local DATA_TIMEOUT_SEC = 0.001

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


local PULL = {}
PULL.wait_list = {}
PULL.result = {}  -- should be a key value pair: key is the uuid of the request, value is the calculated result
PULL.poll_result = {}

local function initData()
	coroutine_flip = 0
	PULL.wait_list = {}
	PULL.result = {}
	PULL.poll_result = {}
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



-- dostring in Export
function dostring_export_env(s)
	local f, err = loadstring(s)
	if f then
		return true, f()
	else
		return false, err
	end
end

function TableConcat(t1,t2)
    for i=1,#t2 do
        t1[#t1+1] = t2[i]
    end
    return t1
end

function table_combine(t1, t2)
	for key, value in pairs(t2) do
		t1[key] = value
	end
	return t1
end



local function step()
	if server then
		server:settimeout(0)  -- give up if no connection
		client = server:accept()  -- accept client
		
		if client then  -- if client not nil, connection established

			local line, err = client:receive()
			if not err then

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
					local bytes, status, lastbyte = client:send(JSON:encode(false) .. '\n')
				end
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
				local success, requests = pcall(
					function()
						return JSON:decode(line)
					end
				)
				if success then  -- run request here

					local readyResult = PULL.brew_result()  -- send nothing back to polling thread
					local bytes, status, lastbyte = poll_client:send(JSON:encode(readyResult) .. '\n')

					process_request(requests)
				else
					env.info(requests)  -- log error
					local readyResult = PULL.prepared_result()
					local bytes, status, lastbyte = poll_client:send(JSON:encode(readyResult) .. '\n')
				end
			end
			
			poll_client:close()  -- done, close connection
			poll_client = nil
		end
	end
end


function parse_export_data(runtime_id, object_data) 
	-- insert runtime id into object_data and add to collection
	object_data["RuntimeID"] = runtime_id
	
	return object_data
end  -- parse_export_data()


-- add key value pair into into result
function get_export_object_data(dict_objects, ...)  -- key value pairs
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


function process_request(requests)  -- runs request from Export Request
	for _, request in pairs(requests) do
		if request.method == HANDLE.QUERY then
			local all_objects = LoGetWorldObjects("units")
			local all_ballistics = LoGetWorldObjects("ballistic")
			
			local all_data = table_combine(all_objects, all_ballistics)
			
			local data_size = 0
			-- get size of key value pair table
			for _,_ in pairs(all_data) do
				data_size = data_size + 1
			end

			-- prepare coroutine
			local total_data_num = data_size
			local batch_size = math.ceil(total_data_num * DATA_TIMEOUT_SEC * 10) -- 10  -- num count of all data divided by 100 (each step)
			local total_batch_num = math.ceil(total_data_num / batch_size)
			
			local co = coroutine.create(get_export_object_data)

			PULL.wait_list[request.id] = {
				["routine"] = co,
				["param"] = all_data,
				["collector"] = PULL.roast_result,
				["batch_size"] = batch_size,
				["total"] = total_data_num
			}  -- function get_export_object_data()
			
		elseif request.method == HANDLE.DEBUG then
			if request.type == "mem" then
				local mem = {}
				mem.size = collectgarbage('count')
				mem.time = os.time()
				
				return mem
			
			elseif request.type == "export_loadstring" then
				local returned, result = dostring_export_env(request.content)
				return result
			
			end
		end

	end
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
			
				local _, res = coroutine.resume(co, param, batch_size)  -- resume coroutine and get yield or return result
				if res then  -- if return / yield is not nil
					collector(uuid, res, total)
				end
			end
		
			
		end
		
	end
end

function LuaExportStart()
	log("Starting DCS Export Server")
	server = assert(socket.bind("127.0.0.1", PORT))
	poll_server = assert(socket.bind("127.0.0.1", POLL_PORT))
	
	local ip, port = server:getsockname()
	local poll_ip, poll_port = poll_server:getsockname()
	
	log("DCS Export Server - Normal Request -> Started on Port " .. port .. " at " .. ip)
	log("DCS Export Server - Polling Request -> Started on Port " .. poll_port .. " at " .. poll_ip)
end

function LuaExportStop()
	if default_output_file then
		default_output_file:close()
		default_output_file = nil
	end

	server:close()
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
