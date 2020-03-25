package moe.ofs.backend.services.jpa;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.handlers.ExportUnitDespawnObservable;
import moe.ofs.backend.handlers.ExportUnitSpawnObservable;
import moe.ofs.backend.logmanager.Logger;
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
    public ExportObjectDeltaJpaService(ExportObjectRepository repository) {
        super(repository);
    }

    @Override
    public void dispose() {
        repository.deleteAll();
        Logger.log("ExportObjectRepository data discarded.");
    }

    @Override
    public void update(ExportObject deltaObject) {
        repository.findByRuntimeID(deltaObject.getRuntimeID()).ifPresent(record -> {
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
            if(deltaObject.getLatLongAlt() != null) {
                deltaObject.getLatLongAlt().forEach((key, value) -> record.getLatLongAlt().put(key, value));
            }
            if(deltaObject.getPosition() != null) {
                deltaObject.getPosition().forEach((key, value) -> record.getPosition().put(key, value));
            }

            repository.save(record);
        });
    }

    @Override
    public void add(ExportObject deltaObject) {
        repository.save(deltaObject);
        ExportUnitSpawnObservable.invokeAll(deltaObject);
    }

    @Override
    public void remove(ExportObject deltaObject) {
        Optional<ExportObject> optional = repository.findByRuntimeID(deltaObject.getRuntimeID());
        optional.ifPresent(obsoleteObject -> {
            ExportUnitDespawnObservable.invokeAll(obsoleteObject);
            repository.delete(optional.get());
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
