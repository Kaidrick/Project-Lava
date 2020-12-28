local JSON = require("JSON")

-- definition contains: name, predicate function, storage name, hook type

local hook_name = '%s'
local definition_name = '%s'
local store_name = '%s'  -- storage table / kw name
local f = %s  -- predicate function
local fm = %s  -- predicate result mapping function
local fp = %s  -- raw args processor function

local hook = _G[hook_name]  -- reference to callback table

hook.predicates[definition_name] = {
    test = f,
    store = __storage[store_name],
    mapper = fm,
    processor = fp
}

return true