function(event)
    local data = {}
    if event.initiator and event.initiator.id_ then data.initiatorId = event.initiator.id_ end
    if event.weapon and event.weapon.id_ then data.weaponId = event.weapon.id_ end
    if event.weapon_name then data.weaponName = event.weapon_name end
    if event.target and event.target.id_ then data.targetId = event.target.id_ end
    if event.id then data.id = event.id end
    if event.time then data.time = event.time end

    return data
end