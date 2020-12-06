local JSON = require("JSON")

__storage = __storage or {}

-- get table by name
local pair_name = '%s'
local key_json = '%s'

local key = JSON:decode(key_json)

local db_pair = __storage[pair_name]

if db_pair then
    local data = db_pair:get(key)
    local dataString = JSON:encode(data)

    net.log(dataString)

    db_pair:delete(key)

    return dataString
end