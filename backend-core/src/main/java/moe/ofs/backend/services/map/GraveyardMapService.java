package moe.ofs.backend.services.map;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.domain.GraveyardRecord;
import moe.ofs.backend.services.CrudService;
import moe.ofs.backend.services.GraveyardService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class GraveyardMapService extends AbstractMapService<GraveyardRecord>
        implements GraveyardService, CrudService<GraveyardRecord> {

    @Value("${lava.graveyard-persistent-duration:120}")
    private Long persistentDuration;

    @Override
    public void collect(ExportObject record) {
        GraveyardRecord graveyardRecord =
                new GraveyardRecord(record, Instant.now().plusSeconds(persistentDuration), Instant.now());
        save(graveyardRecord);
    }

    @Override
    @Scheduled(fixedDelay = 10000L)
    public void dispose() {
        map.values().stream().filter(this::isExpired).forEach(record -> deleteById(record.getId()));
    }

    @Override
    public Optional<GraveyardRecord> findByRecordRuntimeId(long runtimeId) {
        return findAll().parallelStream().filter(record -> record.getRecord().getRuntimeID() == runtimeId).findAny();
    }

    private boolean isExpired(GraveyardRecord record) {
        return Instant.now().isAfter(record.getExpirationTime());
    }


}
