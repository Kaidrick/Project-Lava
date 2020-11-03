package moe.ofs.backend.services.jpa;

import com.google.common.collect.Sets;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.LavaLog;
import moe.ofs.backend.lavalog.eventlogger.SpawnControlLogger;
import moe.ofs.backend.handlers.PlayerEnterServerObservable;
import moe.ofs.backend.handlers.PlayerLeaveServerObservable;
import moe.ofs.backend.handlers.PlayerSlotChangeObservable;
import moe.ofs.backend.repositories.PlayerInfoRepository;
import moe.ofs.backend.services.PlayerInfoService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlayerInfoJpaService extends AbstractJpaService<PlayerInfo, PlayerInfoRepository>
        implements PlayerInfoService {

    private final LavaLog.Logger logger = LavaLog.getLogger(SpawnControlLogger.class);

    public PlayerInfoJpaService(PlayerInfoRepository repository) {
        super(repository);
    }

    @Override
    public void dispose() {
        repository.deleteAll();
        logger.log("PlayerInfoRepository data discarded.");
    }

    @Override
    public Optional<PlayerInfo> findByUcid(String ucid) {
        return repository.findByUcid(ucid);
    }

    @Override
    public Optional<PlayerInfo> findByNetId(int netId) {
        return repository.findByNetId(netId);
    }

    @Override
    public PlayerInfo update(PlayerInfo updateObject) {
        PlayerInfo recordObject = repository.findByUcid(updateObject.getUcid())
                .orElseThrow(() -> new RuntimeException("Unable to find PlayerInfo with UCID: " +
                        updateObject.getUcid()));

        PlayerInfo previous = new PlayerInfo(recordObject);

        if(updatable(updateObject, recordObject)) {

            recordObject.setPing(updateObject.getPing());
            recordObject.setSide(updateObject.getSide());

            if(!recordObject.getSlot().equals(updateObject.getSlot())) {

//                PlayerSlotChangeObservable.invokeAll(recordObject, updateObject);
//                sender.sendToTopic("player.connection", new PlayerInfo[] {recordObject, updateObject},
//                        "slotchange");
                recordObject.setSlot(updateObject.getSlot());
            }

            repository.save(recordObject);
        }

        return previous;
    }

    @Override
    public boolean detectSlotChange(PlayerInfo recordObject, PlayerInfo updateObject) {
        if (recordObject.getSlot().equals(updateObject.getSlot())) {
            PlayerSlotChangeObservable.invokeAll(recordObject, updateObject);
            return false;
        }
        return true;
    }

    @Override
    public void add(PlayerInfo newObject) {
        repository.save(newObject);
        PlayerEnterServerObservable.invokeAll(newObject);
//        sender.sendToTopic("player.connection", newObject, "connect");
    }

    @Override
    public void remove(PlayerInfo obsoleteObject) {
        repository.delete(obsoleteObject);
        PlayerLeaveServerObservable.invokeAll(obsoleteObject);
//        sender.sendToTopic("player.connection", obsoleteObject, "disconnect");
    }

    @Override
    public void cycle(List<PlayerInfo> list) {
        Set<PlayerInfo> record = repository.findAll().parallelStream().collect(Collectors.toSet());
        Set<PlayerInfo> update = new HashSet<>(list);

        Sets.SetView<PlayerInfo> intersection = Sets.intersection(record, update);
        Sets.SetView<PlayerInfo> obsoletePlayers = Sets.symmetricDifference(intersection, record);
        Sets.SetView<PlayerInfo> newPlayers = Sets.symmetricDifference(intersection, update);

        // intersection contains old data, need updated info instead
        intersection.forEach(o -> update(update.stream()
                .filter(playerInfo -> playerInfo.equals(o)).findFirst()
                .orElseThrow(() -> new RuntimeException("Unable to find target record PlayerInfo"))));

//        intersection.forEach(this::processUpdateData);
        obsoletePlayers.forEach(this::remove);
        newPlayers.forEach(this::add);
    }

    @Override
    public boolean updatable(PlayerInfo update, PlayerInfo record) {
        return !record.getSlot().equals(update.getSlot()) ||
                record.getPing() != update.getPing() ||
                record.getSide() != update.getSide();
    }
}
