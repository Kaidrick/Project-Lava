package moe.ofs.backend.function.spawncontrol.services;

import moe.ofs.backend.domain.dcs.poll.ExportObject;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;

import java.util.Optional;

public interface SpawnService {
    boolean isPlayerSpawned(PlayerInfo playerInfo);

    Optional<PlayerInfo> getUnitOccupant(ExportObject exportObject);
}
