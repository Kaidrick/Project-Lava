local JSON = require("JSON")
local inspect = require("inspect")
local dataString = '%s'

local country = country.id.USA

local group_data = JSON:decode(dataString)

-- fix callsign table
for unit_index, unit_table in pairs(group_data.units) do
    local corrected_callsign = {}
    local obsolete_callsign = unit_table.callsign

    corrected_callsign[1] = obsolete_callsign["1"]
    corrected_callsign[2] = obsolete_callsign["2"]
    corrected_callsign[3] = obsolete_callsign["3"]
    corrected_callsign["name"] = obsolete_callsign["name"]

    group_data.units[unit_index].callsign = corrected_callsign
end

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