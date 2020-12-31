function(addr, name, ucid, playerID)
    local table_name = '%s'
    local data_table = __storage[table_name]

    for rejectUcid, data in pairs(data_table:entries()) do
        if ucid == rejectUcid then
            return false
        end
    end
end