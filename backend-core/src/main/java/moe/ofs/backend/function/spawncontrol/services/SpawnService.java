package moe.ofs.backend.function.spawncontrol.services;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.domain.PlayerInfo;

import java.util.Optional;

public interface SpawnService {
    boolean isPlayerSpawned(PlayerInfo playerInfo);

    Optional<PlayerInfo> getUnitOccupant(ExportObject exportObject);
}
