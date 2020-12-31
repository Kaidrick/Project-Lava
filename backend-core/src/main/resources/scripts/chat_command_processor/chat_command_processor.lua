local JSON = require("JSON")

chat_command_processor = {}

chat_command_processor._keywords = {}

chat_command_processor._commands = {}

chat_command_processor.fetch_commands = function()
    local jsonData = JSON:encode(chat_command_processor._commands)
    chat_command_processor._commands = {}
    return jsonData
end

local function starts_with(str, start)
   return str:sub(1, #start) == start
end

local function ends_with(str, ending)
   return ending == "" or str:sub(-#ending) == ending
end

chat_command_processor.onPlayerTrySendChat = function(playerID, msg, all)
    for _, keyword in pairs(chat_command_processor._keywords) do
        if starts_with(msg, keyword) then
            local data = {
                playerID = playerID,
                msg = msg,
                time = os.time(),
                kw = keyword
            }
            table.insert(chat_command_processor._commands, data)
            return ""  -- drop the message from chat
        end
    end

    return msg
end

DCS.setUserCallbacks(chat_command_processor)