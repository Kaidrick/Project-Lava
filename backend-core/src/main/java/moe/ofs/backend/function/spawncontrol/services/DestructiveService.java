package moe.ofs.backend.function.spawncontrol.services;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.domain.PlayerInfo;

public interface DestructiveService {
    void destroy(ExportObject exportObject);

    void destroy(PlayerInfo playerInfo);

    void explode(ExportObject exportObject);

    void explode(PlayerInfo playerInfo);
}
