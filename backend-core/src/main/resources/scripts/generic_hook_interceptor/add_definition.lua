-- definition contains: name, predicate function, storage name, hook type

local hook_name = '%s'
local definition_name = '%s'
local target_function_name = '%s'  -- get from hook type
local store_name = %s  -- storage table / kw name
local f = %s  -- predicate function

_G[hook_name] = {}  -- ensuring user callback table exists

local hook = _G[hook_name]  -- reference to callback table

hook.predicate[target_function_name] = hook.predicate[target_function_name] or {}  -- ensuring predicate exists

-- if function is not defined, defined first
if not hook[target_function_name] then
    hook[target_function_name] = function(...)
        for _, predicate in pairs(hook.predicate[target_function_name]) do
            local res = {predicate(__storage[store_name], ...)}
            if res ~= nil then  -- nil indicate no return, which means continue in next predicate in chain
                return unpack(res)
            end  -- otherwise do nothing
        end
    end
end

-- after ensuring target function hook exists, add to predicate
hook.predicate[target_function_name][definition_name] = f
