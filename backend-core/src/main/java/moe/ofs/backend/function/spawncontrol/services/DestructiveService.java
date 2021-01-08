package moe.ofs.backend.function.spawncontrol.services;

import moe.ofs.backend.domain.dcs.poll.ExportObject;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;

public interface DestructiveService {
    void destroy(ExportObject exportObject);

    boolean destroy(PlayerInfo playerInfo);

    void explode(ExportObject exportObject);

    void explode(PlayerInfo playerInfo);
}
