local db_table_name = '%s'

local requests = DataTable:allocate(db_table_name .. '_request')
local slotMoves = DataTable:allocate(db_table_name .. '_slot_move')

slot_validator = {}

--[[
    criteria are functions that are run for each try change slot action.
    if any of them returns false, reject the slot change immediately
--]]

slot_validator._criteria = {}

slot_validator._slot_confirm_callback = {}

slot_validator.fetch_records = function()
    local array = {}

    for _, req in pairs(requests:findAll()) do
        local data = {
            player_id = req[1],    -- number
            side = req[2],        -- number
            cur_side = req[3],    -- number
            slot_id = req[4],     -- string
            cur_slot_id = req[5]  -- string
        }
        table.insert(array, data)
    end
    requests:deleteAll()

    for _, mv in pairs(slotMoves:findAll()) do
        local data = {
            player_id = mv[1],    -- number
            cur_side = mv[2],    -- number
            cur_slot_id = mv[3]  -- string

        table.insert(array, data)
    end
    slotMoves:deleteAll()

    return array
end

slot_validator.fetch_slot_move = function()
    local array = {}
    for _, mv in pairs(slotMoves:findAll()) do
        local data = {
            player_id = mv[1],    -- number
            cur_side = mv[2],    -- number
            cur_slot_id = mv[3]  -- string

        table.insert(array, data)
    end
    slotMoves:deleteAll()

    return array
end

slot_validator.fetch_slot_request = function()
    local array = {}
    for _, req in pairs(requests:findAll()) do
        local data = {
            player_id = req[1],    -- number
            side = req[2],        -- number
            cur_side = req[3],    -- number
            slot_id = req[4],     -- string
            cur_slot_id = req[5]  -- string
        }
        table.insert(array, data)
    end

    requests:deleteAll()
    return array
end

slot_validator.onPlayerTryChangeSlot = function(playerID, side, slotID) -- -> true | false

    -- collect try change slot info and push to _slot_move table
    -- but if __lava_hand_off then no need to record any info
    -- pass control to next validator in chain

    local current_side, current_slotID = net.get_slot(playerID)
    local data = { playerID, side, current_side, slotID, current_slotID }
    requests:save(data)

    -- if completely handed off, then return immediately
    if __lava_hand_off then
        return
    else
        for _, predicate in pairs(slot_validator._criteria) do
            if not predicate(playerID, side, slotID) then
                return false
            end
        end
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

    slotMoves:save({ id, current_side, current_slotID })
end

DCS.setUserCallbacks(slot_validator)