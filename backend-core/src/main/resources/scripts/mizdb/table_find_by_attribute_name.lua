local JSON = require("JSON")

missionDatabase = missionDatabase or {}

-- get table by name
local table_name = '%s'
local attribute_name = '%s'
local value = '%s'

local db_table = missionDatabase[table_name]

if db_table then
    return JSON:encode(db_table:findBy(attribute_name, value))
end