-- global table for storing pairs of groupId and
radio_commands = {}
JSON = JSON or require("JSON")

-- global function for inserting data into radio_commands table
function push_command(group_id,  command_name, parent_path_table)
    local full_path_table = parent_path_table
    table.insert(full_path_table, command_name)
    radio_commands[tostring(group_id)] = full_path_table
end

-- global function for getting data from radio_commands table
function query_commands()
    local result = radio_commands
    radio_commands = {}
    return JSON:encode(result)
end