local hook_name = '%s'
local target_function_name = '%s'
local player_id_arg_index = %d

local JSON = require("JSON")

if _G[hook_name] then
    return  -- already initialized
end

_G[hook_name] = _G[hook_name] or {  -- should only be initialized once to avoid duplicated callbacks
    predicates = {},  -- creation is always destructive
    decisions = DataTable:allocate(hook_name)  -- used to store predicate results
}  -- create hook lua table

_G[hook_name][target_function_name] = function(...)
    local args = {...}
    local player_id = args[player_id_arg_index]  -- extract player net id

    for def_name, predicate in pairs(_G[hook_name].predicates) do
        local res = {predicate.test(predicate.store, ...)}
        local verdict = {  -- TODO can be a custom mapper
            __entity_player_id = player_id,
            __entity_definition_name = def_name,
            __entity_target = target_function_name,
            __entity_time = os.time()
        }

        if predicate.mapper then
            verdict.__predicate_result = JSON:encode(predicate.mapper(res))
        else
            verdict.__predicate_result = JSON:encode(res)
        end

        _G[hook_name].decisions:save(verdict)

        if #res > 0 then
            return unpack(res)
        end
    end

    net.log(target_function_name .. " + test end")
end

DCS.setUserCallbacks(_G[hook_name])
