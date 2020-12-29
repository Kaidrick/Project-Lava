package moe.ofs.backend.services;

import java.util.Map;

public interface LuaStorageInitService {
    Map<MissionPersistenceService, Boolean> initStorages();
}
