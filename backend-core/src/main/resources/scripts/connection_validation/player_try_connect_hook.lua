connection_validator = {}

connection_validator.predicates = {}

connection_validator.onPlayerTryConnect = function(addr, name, ucid, playerID)
    for _, predicate in pairs(connection_validator.predicates) do
        local allow, reason = predicate(addr, name, ucid, playerID)
        if not allow then
            return false, reason
        end
        --if not predicate(addr, name, ucid, playerID) then return false end  -- failed validate test
    end
end

DCS.setUserCallbacks(connection_validator)
