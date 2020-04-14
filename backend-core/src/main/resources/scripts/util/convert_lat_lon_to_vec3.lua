local JSON = require("JSON")

local longitude = %f
local latitude = %f

return JSON:encode(Export.LoGeoCoordinatesToLoCoordinates(longitude, latitude))