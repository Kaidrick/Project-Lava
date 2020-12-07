local JSON = require("JSON")

if slot_change_manager then
    return JSON:encode(slot_change_manager.fetch_slot_move())
end