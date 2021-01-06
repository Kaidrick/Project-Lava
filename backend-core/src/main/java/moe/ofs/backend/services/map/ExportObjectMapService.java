package moe.ofs.backend.services.map;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.repositories.ExportObjectRepository;
import moe.ofs.backend.services.ExportObjectNotFoundException;
import moe.ofs.backend.services.ExportObjectService;
import moe.ofs.backend.services.UpdatableService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
//@Primary
@Slf4j
public class ExportObjectMapService extends AbstractMapService<ExportObject>
        implements ExportObjectRepository, UpdatableService<ExportObject>, ExportObjectService {

    public ExportObjectMapService() {
    }

    @Override
    public Optional<ExportObject> findByUnitName(String name) {
        return findAll().stream().filter(exportObject -> name.equals(exportObject.getUnitName())).findAny();
    }

    @Override
    public Optional<ExportObject> findByRuntimeId(Long runtimeId) {
        return findAll().stream().filter(exportObject -> exportObject.getRuntimeID() == runtimeId).findAny();
    }

    @Override
    public Optional<ExportObject> findByRuntimeId(String runtimeIdString) {
        long runtimeId = Long.parseLong(runtimeIdString);
        return findByRuntimeID(runtimeId);
    }

    @Override
    public Optional<ExportObject> findByRuntimeID(Long runtimeId) {
        return findAll().stream().filter(exportObject -> exportObject.getRuntimeID() == runtimeId).findAny();
    }

    /**
     * TODO: is delete by runtime id really that often?
     *
     * @param runtimeId Unique runtime id of the object
     */
    @Override
    public void deleteByRuntimeID(Long runtimeId) {
        Optional<ExportObject> optional = findByRuntimeID(runtimeId);
        optional.ifPresent(this::remove);
    }

    /**
     * TODO: since update is rather frequent, maybe use a hash map to store runtime id -> Long id mapping?
     * TODO: what are the potential concurrent issues?
     * Maybe there is an api request that searches the map while the object has just been destroy
     * But export object can only be deleted by 'delete' command from lua; need more investigation
     *
     * @param updateObject the export object whose values are to be insert to record object
     * @return ExportObject which is the updated record object
     */
    @Override
    public ExportObject update(ExportObject updateObject) {
        Optional<ExportObject> optional = findByRuntimeID(updateObject.getRuntimeID());
        optional.ifPresent(exportObject -> {
            if (updateObject.getPosition() != null) {
                exportObject.setPosition(updateObject.getPosition());
            }

            List<String> updatedFields = updateFields(exportObject, updateObject);
//            log.info("{} fields updated for {}: {}", updatedFields.size(), exportObject.getRuntimeID(),
//                    String.join(", ", updatedFields));
        });

        return optional.orElseThrow(() -> {
            String message = "No export object can be found matching update runtime id: " +
                    updateObject.getRuntimeID();
            return new ExportObjectNotFoundException(message);
        });
    }

    @Override
    public void add(ExportObject newObject) {
        save(newObject);
    }

    @Override
    public void remove(ExportObject obsoleteObject) {
        delete(obsoleteObject);
    }

    @Override
    public void cycle(List<ExportObject> list) {
        // do nothing
    }

    @Override
    public boolean updatable(ExportObject update, ExportObject record) {
        return false;
    }

    @Override
    public void dispose() {
        deleteAll();
    }
}
