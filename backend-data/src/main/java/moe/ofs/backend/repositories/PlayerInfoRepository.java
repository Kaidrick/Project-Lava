package moe.ofs.backend.repositories;

import moe.ofs.backend.domain.PlayerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
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
