-- collectgarbage()
local JSON = require("JSON")
local report = {
    usage = collectgarbage('count'),
    os_time = os.time()
}
return JSON:encode(report)
