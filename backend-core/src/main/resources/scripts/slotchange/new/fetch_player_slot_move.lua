local JSON = require("JSON")

if slot_validator then
    return JSON:encode(slot_validator.fetch_slot_move())
end