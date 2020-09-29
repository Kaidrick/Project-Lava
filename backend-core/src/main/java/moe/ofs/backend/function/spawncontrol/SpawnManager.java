package moe.ofs.backend.function.spawncontrol;

import moe.ofs.backend.object.Vector3D;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.util.LuaScripts;

public interface SpawnManager {
    /**
     * Pull data from DCS and check if there is already an sim object
     * near the given Vector3D and return the result as a boolean
     * @param vector3D the point to be check
     * @return boolean value representing whether it is safe to spawn a unit to this point
     */
    default boolean isSafeSpawn(Vector3D vector3D, double safetyRange) {

        String query = LuaScripts.loadAndPrepare("util/search_object.lua",
                vector3D.getX(), vector3D.getY(), vector3D.getZ(), safetyRange);

        return !new ServerDataRequest(query).getAsBoolean();

    }
}
