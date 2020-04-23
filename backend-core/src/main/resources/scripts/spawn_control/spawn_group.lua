local JSON = require("JSON")
local dataString = '%s'

local country = country.id.USA

local group_data = JSON:decode(dataString)
coalition.addGroup(country, group_data.category, group_data)

local this_group = Group.getByName(group_data.name)
local group_units = this_group:getUnits()

local spawn_info = {
    group_runtime_id = this_group.id_,
    group_unit_name_id_pairs = {}
}

for idx, unit in pairs(group_units) do
  spawn_info.group_unit_name_id_pairs[unit:getName()] = unit.id_
end

return JSON:encode(spawn_info)