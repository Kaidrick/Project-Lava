package moe.ofs.backend.services.map;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.repositories.PlayerInfoRepository;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.services.UpdatableService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class PlayerInfoMapService extends AbstractMapService<PlayerInfo>
        implements PlayerInfoRepository, UpdatableService<PlayerInfo>, PlayerInfoService {
    @Override
    public Optional<PlayerInfo> findByName(String playerName) {
        return Optional.empty();
    }

    @Override
    public Optional<PlayerInfo> findByNetId(int netId) {
        return Optional.empty();
    }

    @Override
    public boolean detectSlotChange(PlayerInfo previous, PlayerInfo current) {
        return false;  // TODO
    }

    @Override
    public List<PlayerInfo> findAllByLang(String lang) {
        return null;
    }

    @Override
    public void dispose() {
        map.clear();
    }

    @Override
    public Optional<PlayerInfo> findByUcid(String ucid) {
        return Optional.empty();
    }

    @Override
    public Optional<PlayerInfo> findBySlot(String slot) {
        return Optional.empty();
    }

    @Override
    public Optional<PlayerInfo> findAllBySide(int side) {
        return Optional.empty();
    }

    @Override
    public Optional<PlayerInfo> findByPingGreaterThan(int ping) {
        return Optional.empty();
    }

    @Override
    public PlayerInfo update(PlayerInfo updateObject) {
        return null;
    }

    @Override
    public void add(PlayerInfo newObject) {

    }

    @Override
    public void remove(PlayerInfo obsoleteObject) {

    }

    @Override
    public void cycle(List<PlayerInfo> list) {

    }

    @Override
    public boolean updatable(PlayerInfo update, PlayerInfo record) {
        return false;
    }
}
