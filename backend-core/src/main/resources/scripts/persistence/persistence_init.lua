lava_mission_persistent_initialization = false

--- Registers an event handler.
-- @tparam function f function handling event
-- @treturn number id of the event handler
local idNum = 0
function add_event_handler(f) --id is optional!
	local handler = {}
	idNum = idNum + 1
	handler.id = idNum
	handler.f = f
	function handler:onEvent(event)
		self.f(event)
	end
	world.addEventHandler(handler)
	return handler.id
end

--- Removes event handler with given id.
-- @tparam number id event handler id
-- @treturn boolean true on success, false otherwise
function remove_event_handler(id)
	for key, handler in pairs(world.eventHandlers) do
		if handler.id and handler.id == id then
			world.eventHandlers[key] = nil
			return true
		end
	end
	return false
end



function init_persistence(event)  -- reset flag on mission start / restart
    if event.id == 11 then
        env.info('mission restart event ' .. os.time())
        lava_mission_persistent_initialization = false
    end
end

local handler = {}
handler.f = init_persistence
function handler:onEvent(event)
    self.f(event)
end

world.addEventHandler(handler)



local inspect = require("inspect")

world.eventHandlers = {}

return inspect(world)