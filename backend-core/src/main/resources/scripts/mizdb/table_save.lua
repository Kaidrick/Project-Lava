local JSON = require("JSON")

missionDatabase = missionDatabase or {}

-- get table by name
local table_name = '%s'
local object_json = '%s'
local object = JSON:decode(object_json)

local db_table = missionDatabase[table_name]

if db_table then
    return db_table:save(object)
end
