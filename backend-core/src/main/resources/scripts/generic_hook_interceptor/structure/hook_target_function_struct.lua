_G[hook_name].${hookFunctionName} = function(${hookArgsString})
    for _, predicate in pairs(_G[hook_name].predicate['${hookFunctionName}']) do
        if predicate(${hookArgsString}, _G[hook_name].store) == false then  -- else passed validation
            return false
        end
    end
end