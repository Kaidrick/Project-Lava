local JSON = require("JSON")

missionDatabase = missionDatabase or {}

-- get table by name
local table_name = '%s'

-- mapping function
local f = %s

local db_table = missionDatabase[table_name]

if db_table then
    local data = db_table:findAll()

    for index, datum in pairs(data) do
        data[index] = f(datum)
    end

    local dataString = JSON:encode(data)

    db_table:deleteAll()

    return dataString
end