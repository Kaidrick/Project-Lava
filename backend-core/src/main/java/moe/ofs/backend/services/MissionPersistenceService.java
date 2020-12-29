package moe.ofs.backend.services;

import java.util.ArrayList;
import java.util.List;

/**
 * MissionPersistenceService uses a specific lua table in mission runtime as a data repository
 */
public interface MissionPersistenceService {
    List<MissionPersistenceService> list = new ArrayList<>();

    void resetRepository();

    default boolean createRepository() {
        return list.add(this);
    }

    String getRepositoryName();
}
