package moe.ofs.backend.services;

import moe.ofs.backend.object.ExportObject;

import java.util.Map;
import java.util.Optional;

public interface ExportObjectService extends ObjectRepositoryService<ExportObject> {

    Optional<ExportObject> findByRuntimeId(long runtimeId);

    Optional<ExportObject> findByUnitName(String unitName);

    void deleteByRuntimeId(long runtimeId);

    void delete(ExportObject object);

    Map<Long, ExportObject> findAll();
}
