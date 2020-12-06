local JSON = require("JSON")

__storage = __storage or {}

-- get table by name
local pair_name = '%s'
local key_json = '%s'

local key = JSON:decode(key_json)

local db_pair = __storage[pair_name]

if db_pair then
    return JSON:encode(db_pair:get(key))
end