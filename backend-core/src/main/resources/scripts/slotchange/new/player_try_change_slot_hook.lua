slot_validator = {}

slot_validator._slot_move = {}

--[[
    criteria are functions that are run for each try change slot action.
    if any of them returns false, reject the slot change immediately
--]]

slot_validator._criteria = {}

slot_validator._slot_confirm_callback = {}

slot_validator._rejection = {}  -- rejection log

slot_validator.fetch_slot_move = function()
    local array = {}
    for playerID, attr in pairs(slot_validator._slot_move) do
        local data = {
            player_id = playerID,  -- number
            side = attr[1],        -- number
            cur_side = attr[2],    -- number
            slot_id = attr[3],     -- string
            cur_slot_id = attr[4]  -- string
        }
        table.insert(array, data)
    end
    slot_validator._slot_move = {}
    return array
end

slot_validator.onPlayerTryChangeSlot = function(playerID, side, slotID) -- -> true | false

    -- collect try change slot info and push to _slot_move table
    -- but if __lava_hand_off then no need to record any info
    -- pass control to next validator in chain

    -- if completely handed off, then return immediately
    if __lava_hand_off then
        return
    else
        for _, predicate in pairs(slot_validator._criteria) do
            if not predicate(playerID, side, slotID) then
                return false
            end
        end

        local current_side, current_slotID = net.get_slot(playerID)
        slot_validator._slot_move[playerID] = { side, current_side, slotID, current_slotID }  -- array
    end
end

slot_validator.onPlayerChangeSlot = function(id)  -- net id
    local current_side, current_slotID = net.get_slot(id)

    for _, callback in pairs(slot_validator._slot_confirm_callback) do
        local success, err = pcall(callback, id, current_side, current_slotID)
        if not success then
            net.log(err)
        end
    end
end

DCS.setUserCallbacks(slot_validator)