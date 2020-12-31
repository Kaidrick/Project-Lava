package moe.ofs.backend.function.mizdb.services;

import java.util.ArrayList;
import java.util.List;

/**
 * MissionPersistenceService uses a specific Lua table (_G[__storage]) in mission runtime as a data repository.
 * Its usage can also be extended to other lua states specified in {@link moe.ofs.backend.hookinterceptor.HookType}.
 *
 * The purpose of lua state storage is to have a centralized place and standard API to manage data that should be
 * checked independently in Lua logic solely or should be erased when a mission is restarted such as time-sensitive
 * or mission specific information.
 */
public interface MissionPersistenceService {
    /**
     * A container list that maintains a list of all defined MissionPersistenceService not managed by Spring context.
     * The list will be iterated through on global Lua storage initialization. See {@link LuaStorageInitService}.
     */
    List<MissionPersistenceService> list = new ArrayList<>();

    /**
     * Explicitly remove all data from the repository. This will take effect immediately in Lua states.
     */
    void resetRepository();

    /**
     * Used to commit the creation of a new Lua storage. This default method will add the implementation into
     * a maintained global list of MissionPersistenceService.
     * @return boolean value indicating whether the creation of this storage is successful. The creation script
     * should return a boolean or a boolean value converted to string to indicate the validness of the operation.
     */
    default boolean createRepository() {
        return list.add(this);
    }

    /**
     * Mandatory for all implementations since a name must be specified for a Lua storage.
     * @return String value that represents the name of this storage. The name of the storage is also used in Lua
     * as the unique identification of the storage table.
     */
    String getRepositoryName();
}
