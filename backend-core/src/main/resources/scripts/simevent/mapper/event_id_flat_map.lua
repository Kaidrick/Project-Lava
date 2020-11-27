local f = function(event)
    local data = {}
    if event.initiator and event.initiator.id_ then data.initiator = event.initiator.id_
    if event.weapon and event.weapon.id_ then data.weapon = event.weapon.id_
    if event.target and event.target.id_ then data.target = event.target.id_
    return data
end