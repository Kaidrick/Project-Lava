__storage = __storage or {}

local table_name = '%s'

-- check for existing table
if __storage[table_name] then
    local db_table = __storage[table_name]
    db_table:deleteAll();
    __storage[table_name] = nil
end
