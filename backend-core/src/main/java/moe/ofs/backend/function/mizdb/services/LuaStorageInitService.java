package moe.ofs.backend.function.mizdb.services;

import java.util.Map;

public interface LuaStorageInitService {
    Map<MissionPersistenceService, Boolean> initStorages();
}
