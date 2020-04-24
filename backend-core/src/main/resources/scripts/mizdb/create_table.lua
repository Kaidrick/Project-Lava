missionDatabase = missionDatabase or {}

local dbName = '%s'

-- check for existing table
if not missionDatabase[dbName] then
    local dataTable = DataTable:new(dbName)
    missionDatabase[dbName] = dataTable
end
