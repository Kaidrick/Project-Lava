package moe.ofs.backend.services;

import moe.ofs.backend.domain.ExportObject;

import java.util.Optional;

public interface ExportObjectService extends UpdatableService<ExportObject>, CrudService<ExportObject> {

    void dispose();

    Optional<ExportObject> findByUnitName(String unitName);

    Optional<ExportObject> findByRuntimeId(Long runtimeId);

    Optional<ExportObject> findByRuntimeId(String runtimeId);

}
