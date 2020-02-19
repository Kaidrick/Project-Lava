local JSON = require("JSON")
local dataString = '%s'

local group_data = JSON:decode(dataString)
coalition.addGroup(country.id.USA, group_data.category, group_data)

local this_group = Group.getByName(group_data.name)
local success, err = pcall(
function(target)
    local pos = target:getUnit(1):getPosition()
    return math.atan2(pos.x.z, pos.x.x)
end, this_group)

if success then
    return err
else
    return -1.000
end


