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
    local current_side, current_slotID = net.get_slot(playerID)
    slot_validator._request[playerID] = { side, current_side, slotID, current_slotID }  -- array
    return false  -- always reject
end

DCS.setUserCallbacks(slot_validator)