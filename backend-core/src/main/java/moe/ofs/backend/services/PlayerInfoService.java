package moe.ofs.backend.services;

import moe.ofs.backend.domain.PlayerInfo;

import java.util.Optional;

public interface PlayerInfoService extends UpdatableService<PlayerInfo>, CrudService<PlayerInfo> {

    void dispose();

    Optional<PlayerInfo> findByUcid(String ucid);

    Optional<PlayerInfo> findByNetId(int netId);
}
