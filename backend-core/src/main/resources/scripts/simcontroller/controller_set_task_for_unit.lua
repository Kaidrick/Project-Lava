local JSON = require("JSON")
local unit = { id_ = %d }
local controller = unit:getController()

controller.setTask(JSON:decode('%s'))