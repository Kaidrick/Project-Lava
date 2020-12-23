local list = { %s }

for _, id in pairs(list) do
    net.send_chat_to([[%s]], id)
end