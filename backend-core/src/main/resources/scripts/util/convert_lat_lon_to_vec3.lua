local JSON = require("JSON")

local latitude = %f
local longitude = %f

return JSON:encode(Export.LoGeoCoordinatesToLoCoordinates(longitude, latitude))