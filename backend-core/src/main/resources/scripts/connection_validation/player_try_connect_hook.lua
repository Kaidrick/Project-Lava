connection_validator = {}

connection_validator.predicates = {}

connection_validator.onPlayerTryConnect = function(addr, name, ucid, playerID)
    for _, predicate in pairs(connection_validator.predicates) do
        if not predicate(addr, name, ucid, playerID) then return false end  -- failed validate test
    end
end

DCS.setUserCallbacks(connection_validator)
