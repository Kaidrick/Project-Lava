missionDatabase = missionDatabase or {}

-- get table by name
local table_name = '%s'
local attribute_name = '%s'
local raw_value = '%s'

-- implicitly convert value to number and check if result if nil
local value = tonumber(raw_value) or raw_value

local db_table = missionDatabase[table_name]

if db_table then
    db_table:deleteBy(attribute_name, value)
end