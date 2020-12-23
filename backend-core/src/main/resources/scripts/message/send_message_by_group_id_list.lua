local list = { %s }  -- list string, msg string, duration, clear view

for _, id in pairs(list) do
    trigger.action.outTextForGroup(id, [[%s]], %d, %b)
end
