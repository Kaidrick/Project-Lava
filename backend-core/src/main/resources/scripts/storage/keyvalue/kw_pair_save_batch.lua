local JSON = require("JSON")

__storage = __storage or {}

-- get table by name
local pair_name = '%s'
local map_json = '%s'  -- json of a map structure
local map_dict = JSON:decode(map_json)

local db_pair = __storage[pair_name]

if db_pair then
  local keys = {}
  for k, v in pairs(map_dict) do
    db_pair:save(k, v)
    table.insert(keys, k)
  end

  return JSON:encode(keys)
end
