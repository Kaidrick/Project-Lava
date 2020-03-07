package moe.ofs.backend.services.jpa;

import com.google.common.collect.Sets;
import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.handlers.ExportUnitDespawnObservable;
import moe.ofs.backend.handlers.ExportUnitSpawnObservable;
import moe.ofs.backend.repositories.ExportObjectRepository;
import moe.ofs.backend.services.ExportObjectService;
import moe.ofs.backend.util.Logger;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExportObjectJpaService extends AbstractJpaService<ExportObject, ExportObjectRepository>
        implements ExportObjectService {

    public ExportObjectJpaService(ExportObjectRepository repository) {
        super(repository);
    }

    @Override
    public void dispose() {
        repository.deleteAll();
        Logger.log("ExportObjectRepository data discarded.");
    }

    @Override
    public void update(ExportObject updateObject) {
        // fetched from db
        ExportObject recordObject = repository.findByRuntimeID(updateObject.getRuntimeID())
                .orElseThrow(() -> new RuntimeException("Unable to find ExportObject with RuntimeID: " +
                        updateObject.getRuntimeID()));

        if(updatable(updateObject, recordObject)) {
            // update recordObject with new data
            recordObject.setFlags(updateObject.getFlags());
            recordObject.setLatLongAlt(updateObject.getLatLongAlt());
            recordObject.setPosition(updateObject.getPosition());
            recordObject.setHeading(updateObject.getHeading());
            recordObject.setBank(updateObject.getBank());
            recordObject.setPitch(updateObject.getPitch());

            repository.save(recordObject);

            // ExportUnitDataChangedObservable? is it necessary? for what will it be used?
        }
    }

    @Override
    public void add(ExportObject newObject) {
        repository.save(newObject);
        ExportUnitSpawnObservable.invokeAll(newObject);
    }

    @Override
    public void remove(ExportObject obsoleteObject) {
        repository.delete(obsoleteObject);
        ExportUnitDespawnObservable.invokeAll(obsoleteObject);
    }

    @Override
    public void cycle(List<ExportObject> list) {
        Set<ExportObject> recordObject =
                repository.findAll().parallelStream()
                        .collect(Collectors.toSet());
        Set<ExportObject> updateObject = new HashSet<>(list);

        // ExportObjects whose data needs to be updated
        Sets.SetView<ExportObject> intersection = Sets.intersection(recordObject, updateObject);

        // new spawn
        // ExportObjects whose data is obsolete to data set and need to be removed
        Sets.SetView<ExportObject> despawn = Sets.symmetricDifference(intersection, recordObject);

        // obsolete despawn
        // ExportObjects whose data is new to data set and need to be added
        Sets.SetView<ExportObject> spawn = Sets.symmetricDifference(intersection, updateObject);

        intersection.forEach(o -> update(updateObject.stream()
                .filter(exportObject -> exportObject.equals(o)).findFirst()
                .orElseThrow(() -> new RuntimeException("Unable to find target record ExportObject"))));

//        intersection.parallelStream().forEach(this::processUpdateData);

        despawn.parallelStream().forEach(this::remove);
        spawn.parallelStream().forEach(this::add);
    }

    @Override
    public boolean updatable(ExportObject update, ExportObject record) {
        return !record.getPosition().equals(update.getPosition()) ||
                record.getBank() != update.getBank() ||
                record.getHeading() != update.getHeading() ||
                record.getPitch() != update.getPitch() ||
                !record.getFlags().equals(update.getFlags());
    }
}
