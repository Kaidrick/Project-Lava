local JSON = require("JSON")
local airbase_list_index = %d
local parking_list_index = %d
local this_airbase = world.getAirbases()[airbase_list_index]
local this_parking = this_airbase:getParking()[parking_list_index]

local terminal_pos = this_parking.vTerminalPos

local lat, lon = coord.LOtoLL(terminal_pos)
local north_pos = coord.LLtoLO(lat + 1, lon)
local north_corr = math.atan2(north_pos.z - terminal_pos.z, north_pos.x - terminal_pos.x)

local parking_data = {
    id = this_parking.Term_Index,
    airdromeName = this_airbase:getName(),
    airdromeId = this_airbase:getID(),  -- airbase_list_index
    position = terminal_pos,
    northCorrection = north_corr,
    terminalType = this_parking.Term_Type,
    metadata = {
        TO_AC = this_parking.TO_AC,
        Term_Index_0 = this_parking.Term_Index_0,
        fDistToRW = this_parking.fDistToRW
    }
}

return JSON:encode(parking_data)
