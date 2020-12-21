local list = %s

for _, id in pairs(list) do
    trigger.action.outTextForGroup(id, '%s', %d, %b)
end
