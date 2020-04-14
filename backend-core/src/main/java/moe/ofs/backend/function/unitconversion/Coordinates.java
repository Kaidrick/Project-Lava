package moe.ofs.backend.function.unitconversion;

import moe.ofs.backend.object.Vector3D;
import moe.ofs.backend.object.map.GeoPosition;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.util.LuaScripts;

public class Coordinates {

    /**
     * Beware that in the dcs lua docs it says
     * {x,y,z} = LoGeoCoordinatesToLoCoordinates(longitude_degrees,latitude_degrees)
     * first longitude, then latitude
     * @param position an instance of GeoPosition indication the geographical coordinate
     * @return Vector3D instance
     */
    public static Vector3D convertToVector3D(GeoPosition position) {
        String json = LuaScripts.loadAndPrepare("util/convert_lat_lon_to_vec3.lua",
                position.getLatitude(), position.getLongitude());

        return new ServerDataRequest(json).getAs(Vector3D.class);
    }

    public static Vector3D convertToVector3D(double latitude, double longitude) {
        String json = LuaScripts.loadAndPrepare("util/convert_lat_lon_to_vec3.lua",
                latitude, longitude);

        return new ServerDataRequest(json).getAs(Vector3D.class);
    }

    public static GeoPosition convertToGeoPosition(Vector3D vector3D) {
        String json = LuaScripts.loadAndPrepare("util/convert_vec3_to_lat_lon.lua",
                vector3D.getX(), vector3D.getZ());

        return new ServerDataRequest(json).getAs(GeoPosition.class);
    }

    public static GeoPosition convertToGeoPosition(double x, double z) {
        String json = LuaScripts.loadAndPrepare("util/convert_vec3_to_lat_lon.lua",
                x, z);

        return new ServerDataRequest(json).getAs(GeoPosition.class);
    }

    // TODO --> batch converter
}
