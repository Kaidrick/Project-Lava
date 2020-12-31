local JSON = require("JSON")

local hook_name = '%s'

local hook = _G[hook_name]

local dataString = JSON:encode(hook.decisions:findAll())
hook.decisions:deleteAll()

return dataString
