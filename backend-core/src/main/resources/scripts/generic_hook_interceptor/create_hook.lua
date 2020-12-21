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

        if #res > 0 then
            local verdict = {  -- TODO can be a custom mapper
                __entity_player_id = player_id,
                __entity_definition_name = def_name,
                __entity_target = target_function_name,
                __entity_time = os.time()
            }

            if predicate.processor then
                for k, v in pairs(predicate.processor(unpack(args))) do
                    verdict[k] = v
                end
            end

            if predicate.mapper then
                for k, v in pairs(predicate.mapper(unpack(res))) do
                    verdict[k] = v
                end
            else
                verdict.__predicate_result = res
            end

            _G[hook_name].decisions:save(verdict)  -- record only when predicate returns a result
            return unpack(res)
        end
    end
end

DCS.setUserCallbacks(_G[hook_name])
