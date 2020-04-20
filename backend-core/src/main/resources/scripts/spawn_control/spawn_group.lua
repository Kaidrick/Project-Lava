local JSON = require("JSON")
local dataString = '%s'

local country = country.id.USA

local group_data = JSON:decode(dataString)
coalition.addGroup(country, group_data.category, group_data)

local this_group = Group.getByName(group_data.name)

return this_group.id_