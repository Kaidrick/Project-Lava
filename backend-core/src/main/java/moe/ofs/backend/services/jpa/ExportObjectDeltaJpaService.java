package moe.ofs.backend.services.jpa;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.function.unitwiselog.LogControl;
import moe.ofs.backend.handlers.ExportUnitDespawnObservable;
import moe.ofs.backend.handlers.ExportUnitSpawnObservable;
import moe.ofs.backend.handlers.ExportUnitUpdateObservable;
import moe.ofs.backend.jms.Sender;
import moe.ofs.backend.repositories.ExportObjectRepository;
import moe.ofs.backend.services.ExportObjectService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Primary
public class ExportObjectDeltaJpaService extends AbstractJpaService<ExportObject, ExportObjectRepository>
        implements ExportObjectService {

    private final Sender sender;

    private final LogControl.Logger logger = LogControl.getLogger(ExportObjectDeltaJpaService.class);

    public ExportObjectDeltaJpaService(ExportObjectRepository repository, Sender sender) {
        super(repository);

        this.sender = sender;
    }

    @Override
    public void dispose() {
        repository.deleteAll();
        logger.log("ExportObjectRepository data discarded.");
    }

    @Override
    public void update(ExportObject deltaObject) {

        repository.findByRuntimeID(deltaObject.getRuntimeID()).ifPresent(record -> {

            ExportObject previous = new ExportObject(record);

            if(deltaObject.getBank() != null) {
                record.setBank(deltaObject.getBank());
            }
            if(deltaObject.getHeading() != null) {
                record.setHeading(deltaObject.getHeading());
            }
            if(deltaObject.getPitch() != null) {
                record.setPitch(deltaObject.getPitch());
            }
            if(deltaObject.getFlags() != null) {
                deltaObject.getFlags().forEach((key, value) -> record.getFlags().put(key, value));
            }
            if(deltaObject.getGeoPosition() != null) {
                record.setGeoPosition(deltaObject.getGeoPosition());
            }
            if(deltaObject.getPosition() != null) {
                record.setPosition(deltaObject.getPosition());
            }

            ExportObject current = new ExportObject(record);

            repository.save(record);

            try {
                ExportUnitUpdateObservable.invokeAll(previous, current);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void add(ExportObject deltaObject) {
        repository.save(deltaObject);
        ExportUnitSpawnObservable.invokeAll(deltaObject);
        sender.sendToTopic("unit.spawncontrol", deltaObject, "spawn");
    }

    @Override
    public void remove(ExportObject deltaObject) {
        Optional<ExportObject> optional = repository.findByRuntimeID(deltaObject.getRuntimeID());
        optional.ifPresent(obsoleteObject -> {
            ExportUnitDespawnObservable.invokeAll(obsoleteObject);
            repository.delete(optional.get());
            sender.sendToTopic("unit.spawncontrol", deltaObject, "despawn");
        });
    }

    @Override
    public void cycle(List<ExportObject> list) {

    }

    @Override
    public boolean updatable(ExportObject update, ExportObject record) {
        return false;
    }
}
