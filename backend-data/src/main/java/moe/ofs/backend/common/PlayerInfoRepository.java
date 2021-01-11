package moe.ofs.backend.common;

import moe.ofs.backend.domain.dcs.poll.PlayerInfo;

import java.util.Optional;
import java.util.Set;

public interface PlayerInfoRepository // extends JpaRepository<PlayerInfo, Long>
{

    Optional<PlayerInfo> findByName(String playerName);

    Optional<PlayerInfo> findByNetId(int netId);

    Set<PlayerInfo> findAllByLang(String lang);

    Optional<PlayerInfo> findByUcid(String ucid);

    Optional<PlayerInfo> findBySlot(String slot);

    Set<PlayerInfo> findAllBySide(int side);

    Set<PlayerInfo> findByPingGreaterThan(int ping);

}
