local JSON = require("JSON")

local vec2 = {x = %d, z = %d}
--local height = land.getHeight(vec2)
--local surfaceType = land.getSurfaceType(vec2)
--local geoPoint = {
--	x = vec2.x,
--  	z = vec2.z,
--  	y = height,
--  	surface = surfaceType,
--}

--return JSON:encode(geoPoint)

local b = {}

for i = 1, 8, 1 do
    for j = 1, 8, 1 do
        table.insert(b, land.getSurfaceType({x = vec2.x + i, z = vec2.z + j}))
    end
end

return JSON:encode(b)