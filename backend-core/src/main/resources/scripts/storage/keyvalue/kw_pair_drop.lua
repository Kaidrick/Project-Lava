__storage = __storage or {}

local pair_name = '%s'

-- check for existing table
if __storage[pair_name] then
    local db_pair = __storage[pair_name]
    db_pair:deleteAll();
    __storage[pair_name] = nil
end
