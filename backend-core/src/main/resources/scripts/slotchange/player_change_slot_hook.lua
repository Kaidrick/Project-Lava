slot_validator = {}

slot_validator._request = {}

slot_validator.clean_request = function()
    slot_validator._request = {}
end

slot_validator.get_request_list = function()
    local array = {}
    for playerID, attr in pairs(slot_validator._request) do
        local data = {
            player_id = playerID,  -- number
            side = attr[1],        -- number
            cur_side = attr[2],    -- number
            slot_id = attr[3],     -- string
            cur_slot_id = attr[4]  -- string
        }
        table.insert(array, data)
    end
    slot_validator.clean_request()
    return array
end

slot_validator.onPlayerTryChangeSlot = function(playerID, side, slotID) -- -> true | false

    -- collect try change slot info and push to _request table
    -- but if __lava_hand_off then no need to record any info
    -- pass control to next validator in chain

    if not __lava_hand_off then
        local current_side, current_slotID = net.get_slot(playerID)
        slot_validator._request[playerID] = { side, current_side, slotID, current_slotID }  -- array
    end

    if __lava_hand_off then
        -- do nothing
    else

    end
end

DCS.setUserCallbacks(slot_validator)