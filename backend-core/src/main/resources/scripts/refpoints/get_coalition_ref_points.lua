local JSON = require("JSON")

local nav_points = env.mission.coalition.blue.nav_points

for idx, pt in pairs(nav_points) do
    local vec3 = {
        x = pt.x,
        y = 0,
        z = pt.y
    }

    local lat, lon, alt = coord.LOtoLL(vec3)
    pt.latitude = lat
    pt.longitude = lon
end

return JSON:encode(nav_points)
