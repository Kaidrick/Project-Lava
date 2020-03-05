package moe.ofs.backend.repositories;

import moe.ofs.backend.object.ExportObject;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ExportObjectHashMapRepository extends AbstractHashMapRepository<ExportObject, Long> {

    // replace BoxOf implementation with proper repository

    public Optional<ExportObject> findByUnitName(String name) {
        return map.values().stream()
                .filter(object -> object.getUnitName().equals(name))
                .findFirst();
    }

    public Optional<ExportObject> findByRuntimeId(long runtimeId) {
        return map.values().stream()
                .filter(object -> object.getRuntimeID() == runtimeId)
                .findFirst();
    }
}
