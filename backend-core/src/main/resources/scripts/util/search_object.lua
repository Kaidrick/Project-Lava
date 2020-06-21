local search_x = %f
local search_y = %f
local search_z = %f
local search_radius = %f

if search_y == 0 then
    search_y = land.getHeight({x = search_x, z = search_z})
end

local search_volume = {
    id = world.VolumeType.SPHERE,
    params = {
        point = {
            x = search_x,
            y = search_y,
            z = search_z
        },
        radius = search_radius
    }
}

local upvalue = false

local if_found = function(item_found)
    upvalue = true
end

world.searchObjects(Object.Category.UNIT, search_volume, if_found)

return tostring(upvalue)