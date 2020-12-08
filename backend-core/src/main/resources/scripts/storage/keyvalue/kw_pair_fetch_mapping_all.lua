local JSON = require("JSON")

__storage = __storage or {}

-- get table by name
local pair_name = '%s'

-- mapping function
local f = %s

local db_pair = __storage[pair_name]

if db_pair then
    local data = db_pair:findAll()

    for index, datum in pairs(data) do
        data[index] = f(datum)
    end

    local dataString = JSON:encode(data)

    db_pair:deleteAll()

    return dataString
end