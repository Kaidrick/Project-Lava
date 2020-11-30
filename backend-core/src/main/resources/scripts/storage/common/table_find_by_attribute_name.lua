local JSON = require("JSON")

__storage = __storage or {}

-- get table by name
local table_name = '%s'
local attribute_name = '%s'
local value = '%s'

local db_table = __storage[table_name]

if db_table then
    return JSON:encode(db_table:findBy(attribute_name, value))
end