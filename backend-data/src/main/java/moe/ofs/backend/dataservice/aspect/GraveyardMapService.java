package moe.ofs.backend.dataservice.aspect;

import moe.ofs.backend.common.AbstractMapService;
import moe.ofs.backend.domain.dcs.poll.ExportObject;
import moe.ofs.backend.domain.GraveyardRecord;
import moe.ofs.backend.common.CrudService;
import moe.ofs.backend.dataservice.GraveyardService;
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
        System.out.println("record = " + record);
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
