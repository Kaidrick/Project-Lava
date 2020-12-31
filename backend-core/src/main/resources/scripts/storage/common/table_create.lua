__storage = __storage or {}

local table_name = '%s'

-- check for existing table
if not __storage[table_name] then
    local dataTable = DataTable:new(table_name)
    __storage[table_name] = dataTable
end

return "true"