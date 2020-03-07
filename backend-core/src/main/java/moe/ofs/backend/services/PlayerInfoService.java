package moe.ofs.backend.services;

import moe.ofs.backend.domain.PlayerInfo;

public interface PlayerInfoService extends UpdatableService<PlayerInfo>, CrudService<PlayerInfo> {

    void dispose();

}
