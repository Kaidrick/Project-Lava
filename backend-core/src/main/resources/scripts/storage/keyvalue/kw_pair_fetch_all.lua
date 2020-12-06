local JSON = require("JSON")

__storage = __storage or {}

-- get table by name
local pair_name = '%s'

local db_pair = __storage[pair_name]

if db_pair then
    local dataString = JSON:encode(db_pair:findAll())
    db_pair:deleteAll()

    return dataString
end
