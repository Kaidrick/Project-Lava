local JSON = require("JSON")

__storage = __storage or {}

-- get table by name
local table_name = '%s'
local object_json = '%s'
local object = JSON:decode(object_json)

local db_table = __storage[table_name]

if db_table then
    return db_table:save(object)
end
