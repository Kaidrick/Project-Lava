local hook_name = '%s'
local useDedicatedStorage = %s  -- save to db or kv storage

_G[hook_name] = {}  -- create hook lua table

-- MOCK
_G[hook_name].predicates = {
    function(%s)  -- signature provided by java
        return id > 5
    end,
}

_G[hook_name].localstorage = {}





-- for each hook defined through lava
-- _G[hook_name].%s(%s)
--
-- end

DCS.setUserCallbacks(_G[hook_name])