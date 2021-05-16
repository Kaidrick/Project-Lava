local table_name = '%s'
--local db_table = missionDatabase[table_name]
local db_table = __storage[table_name] or DataTable:allocate(table_name)
local handler = {}
local collection = {
    [world.event.S_EVENT_SHOT] = true,
    [world.event.S_EVENT_SHOOTING_START] = true,
    [world.event.S_EVENT_SHOOTING_END] = true,
    [world.event.S_EVENT_HIT] = true,
    [world.event.S_EVENT_KILL] = true,

    [world.event.S_EVENT_TAKEOFF] = true,
    [world.event.S_EVENT_LAND] = true,

    [world.event.S_EVENT_CRASH] = true,
    [world.event.S_EVENT_EJECTION] = true,
    --[world.event.S_EVENT_REFUELING] = true,
    --[world.event.S_EVENT_REFUELING_STOP] = true,

    --[world.event.S_EVENT_DEAD] = true,
    [world.event.S_EVENT_PILOT_DEAD] = true,
    [world.event.S_EVENT_UNIT_LOST] = true,
    [world.event.S_EVENT_LANDING_AFTER_EJECTION] = true,


    [world.event.S_EVENT_ENGINE_STARTUP] = true,
    [world.event.S_EVENT_ENGINE_SHUTDOWN] = true,


    --[world.event.S_EVENT_PLAYER_COMMENT] = true
}

handler.ident = "simulationEventCollectorHandler"


-- remove possible duplicate event handler to make the event handler distinct at initialization
for _, eventHandler in pairs(world.eventHandlers) do
    if eventHandler.ident == handler.ident then
    	world.removeEventHandler(eventHandler)
    end
end

handler.f = function(event)
    if collection[event.id] then
        db_table:save(event)
    end
end

function handler:onEvent(event)
    self.f(event)
end

world.addEventHandler(handler)


