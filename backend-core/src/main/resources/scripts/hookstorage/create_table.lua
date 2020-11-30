hookStorage = hookStorage or {}

local storeName = '%s'

-- check for existing table
if not hookStorage[storeName] then
    local dataTable = DataTable:new(storeName)
    hookStorage[storeName] = dataTable
end