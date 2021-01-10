package moe.ofs.backend.dataservice.player;

import moe.ofs.backend.common.CrudService;
import moe.ofs.backend.common.MissionPersistenceRepository;
import moe.ofs.backend.common.UpdatableService;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;

import java.util.Optional;
import java.util.Set;

public interface PlayerInfoService extends UpdatableService<PlayerInfo>, CrudService<PlayerInfo>,
        MissionPersistenceRepository {

    void dispose();

    Optional<PlayerInfo> findByUcid(String ucid);

    Optional<PlayerInfo> findByNetId(int netId);

    Optional<PlayerInfo> findBySlot(String slot);

    Set<PlayerInfo> findAllByLang(String lang);

    Set<PlayerInfo> findAllBySide(int side);

    Set<PlayerInfo> findByPingGreaterThan(int ping);

    Optional<PlayerInfo> findByName(String playerName);

    Set<PlayerInfo> findAll();

    Set<PlayerInfo> findAll(boolean excludeServerHost);

    boolean detectSlotChange(PlayerInfo previous, PlayerInfo current);
}
