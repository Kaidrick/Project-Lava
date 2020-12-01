missionDatabase = missionDatabase or {}

local table_name = '%s'

-- check for existing table
if missionDatabase[table_name] then
    local db_table = missionDatabase[table_name]
    db_table:deleteAll();
    missionDatabase[table_name] = nil
end
