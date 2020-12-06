local JSON = require("JSON")

__storage = __storage or {}

-- get table by name
local pair_name = '%s'
local key_list_json = '%s'  -- json of a map structure
local key_list = JSON:decode(key_list_json)

local db_pair = __storage[pair_name]

if db_pair then
  local keys = {}
  for _, k in pairs(key_list) do
    db_pair:delete(k)
    table.insert(keys, k)
  end

  return JSON:encode(#keys)
end
