package moe.ofs.backend.services;

/**
 * MissionPersistenceService uses a specific lua table in mission runtime as a data repository
 */
public interface MissionPersistenceService {
    void resetRepository();

    void createRepository();

    String getRepositoryName();
}
