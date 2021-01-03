package moe.ofs.backend.services.map;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.services.PlayerDataService;
import moe.ofs.backend.services.UpdatableService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Primary
@Slf4j
public class PlayerDataMapService extends AbstractMapService<PlayerInfo>
        implements UpdatableService<PlayerInfo>, PlayerDataService {

    @Override
    public void dispose() {
        deleteAll();
    }

    @Override
    public Optional<PlayerInfo> findByUcid(String ucid) {
        return findAll().stream().filter(playerInfo -> playerInfo.getUcid().equals(ucid)).findAny();
    }

    @Override
    public Optional<PlayerInfo> findByNetId(int netId) {
        return findAll().stream().filter(playerInfo -> playerInfo.getNetId() == netId).findAny();
    }

    @Override
    public Optional<PlayerInfo> findBySlot(String slot) {
        return findAll().stream().filter(playerInfo -> playerInfo.getSlot().equals(slot)).findAny();
    }

    @Override
    public Set<PlayerInfo> findAllByLang(String lang) {
        return findAll().stream()
                .filter(playerInfo -> playerInfo.getLang().equals(lang))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PlayerInfo> findAllBySide(int side) {
        return findAll().stream().filter(playerInfo -> playerInfo.getSide() == side).collect(Collectors.toSet());
    }

    @Override
    public Set<PlayerInfo> findByPingGreaterThan(int ping) {
        return findAll().stream().filter(playerInfo -> playerInfo.getPing() > ping).collect(Collectors.toSet());
    }

    @Override
    public Optional<PlayerInfo> findByName(String playerName) {
        return findAll().stream().filter(playerInfo -> playerInfo.getName().equals(playerName)).findAny();
    }

    @Override
    public Set<PlayerInfo> findAll(boolean excludeServerHost) {
        return excludeServerHost ? findAll().stream()
                .filter(playerInfo -> playerInfo.getNetId() != 1)
                .collect(Collectors.toSet()) : findAll();
    }

    @Override
    public boolean detectSlotChange(PlayerInfo previous, PlayerInfo current) {
        return previous.getUcid().equals(current.getUcid()) && !previous.getSlot().equals(current.getSlot());
    }

    @Override
    public PlayerInfo update(PlayerInfo updateObject) {
        Optional<PlayerInfo> optionalRecord = findByUcid(updateObject.getUcid());  // search for previous object
        if (optionalRecord.isPresent()) {  // if previous object is found
            PlayerInfo record = new PlayerInfo(optionalRecord.get());  // store previous object
            findByUcid(updateObject.getUcid()).ifPresent(hold -> {  // update previous object to update object values
                hold.setSlot(updateObject.getSlot());
                hold.setNetId(updateObject.getNetId());
                hold.setStarted(updateObject.isStarted());
                hold.setSide(updateObject.getSide());
                hold.setPing(updateObject.getPing());
            });

            return record;  // return previous object
        }

        return optionalRecord.orElse(updateObject);  // if optional record is not present somehow, return update object
    }

    @Override
    public void add(PlayerInfo newObject) {
        save(newObject);
    }

    @Override
    public void remove(PlayerInfo obsoleteObject) {
        delete(obsoleteObject);
    }
}
