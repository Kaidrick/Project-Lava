local inspect = require('inspect')

missionDatabase = missionDatabase or {}

env.info(inspect(missionDatabase))


local dbName = '%s'

-- check for existing table
if not missionDatabase[dbName] then
    local dataTable = DataTable:new(dbName)
    missionDatabase[dbName] = dataTable

    env.info(dbName .. ' created')
else
    env.info(dbName .. ' already exists')
end

env.info(inspect(missionDatabase))
