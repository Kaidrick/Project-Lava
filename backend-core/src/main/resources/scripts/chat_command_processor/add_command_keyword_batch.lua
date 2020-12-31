if chat_command_processor then
    local list = { %s }

    for _, keyword in pairs(list) do
        table.insert(chat_command_processor._keywords, keyword)
    end
end