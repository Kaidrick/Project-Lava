package moe.ofs.backend.config.services;

import lombok.SneakyThrows;
import moe.ofs.backend.config.model.ResetType;

public interface DcsSimulationControlService {
    boolean restart(ResetType type);

    void loadMission(String missionName);

    @SneakyThrows
    default void loadRemoteMissionFile() {
        throw new UnsupportedOperationException();
    }

    void shutdown(ResetType type);
}
