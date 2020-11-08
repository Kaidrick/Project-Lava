package moe.ofs.backend.services.map;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.repositories.ExportObjectRepository;
import moe.ofs.backend.services.ExportObjectService;
import moe.ofs.backend.services.UpdatableService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class ExportObjectMapService extends AbstractMapService<ExportObject>
        implements ExportObjectRepository, UpdatableService<ExportObject>, ExportObjectService {
    @Override
    public Optional<ExportObject> findByUnitName(String name) {
        return map.values().stream().filter(exportObject -> exportObject.getUnitName().equals(name)).findAny();
    }

    @Override
    public Optional<ExportObject> findByRuntimeID(Long runtimeId) {
        return map.values().stream().filter(exportObject -> exportObject.getRuntimeID() == runtimeId).findAny();
    }

    /**
     * TODO: is delete by runtime id really that often?
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
     * @param updateObject
     * @return
     */
    @Override
    public ExportObject update(ExportObject updateObject) {
        // find id and then update
//        Long id = map.entrySet().stream()
//                .filter(entry -> entry.getValue().equals(updateObject)).findAny()
//                .orElseThrow(RuntimeException::new).getKey();
//        return map.put(id, updateObject);
        return updateObject;
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
        map.clear();
    }
}
