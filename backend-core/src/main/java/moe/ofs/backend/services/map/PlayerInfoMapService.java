package moe.ofs.backend.services.map;

import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.repositories.PlayerInfoRepository;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.services.UpdatableService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Primary
public class PlayerInfoMapService extends AbstractMapService<PlayerInfo>
        implements PlayerInfoRepository, UpdatableService<PlayerInfo>, PlayerInfoService {
    @Override
    public Optional<PlayerInfo> findByName(String playerName) {
        return map.values().stream().filter(playerInfo -> playerInfo.getName().equals(playerName)).findAny();
    }

    @Override
    public Optional<PlayerInfo> findByNetId(int netId) {
        return map.values().stream().filter(playerInfo -> playerInfo.getNetId() == netId).findAny();
    }

    @Override
    public boolean detectSlotChange(PlayerInfo previous, PlayerInfo current) {
        return false;
    }

    @Override
    public Set<PlayerInfo> findAll() {
        return new HashSet<>(map.values());
    }

    @Override
    public Set<PlayerInfo> findAllByLang(String lang) {
        return map.values().stream()
                .filter(playerInfo -> playerInfo.getLang().equals(lang))
                .collect(Collectors.toSet());
    }

    @Override
    public void dispose() {
        map.clear();
    }

    @Override
    public Optional<PlayerInfo> findByUcid(String ucid) {
        return map.values().stream().filter(playerInfo -> playerInfo.getUcid().equals(ucid)).findAny();
    }

    @Override
    public Optional<PlayerInfo> findBySlot(String slot) {
        return map.values().stream().filter(playerInfo -> playerInfo.getSlot().equals(slot)).findAny();
    }

    @Override
    public Set<PlayerInfo> findAllBySide(int side) {
        return map.values().stream().filter(playerInfo -> playerInfo.getSide() == side).collect(Collectors.toSet());
    }

    @Override
    public Set<PlayerInfo> findByPingGreaterThan(int ping) {
        return map.values().stream().filter(playerInfo -> playerInfo.getPing() > ping).collect(Collectors.toSet());
    }

    @Override
    public PlayerInfo update(PlayerInfo updateObject) {
        return null;
    }

    @Override
    public void add(PlayerInfo newObject) {
        save(newObject);
    }

    @Override
    public void remove(PlayerInfo obsoleteObject) {
        delete(obsoleteObject);
    }

    @Override
    public void cycle(List<PlayerInfo> list) {

    }

    @Override
    public boolean updatable(PlayerInfo update, PlayerInfo record) {
        return false;
    }
}
