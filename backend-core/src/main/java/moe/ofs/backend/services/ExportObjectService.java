package moe.ofs.backend.services;

import moe.ofs.backend.object.ExportObject;

import java.util.Optional;

public interface ExportObjectService {

    ExportObject save(ExportObject object);

    ExportObject update(ExportObject object);

    void delete(ExportObject object);

    Optional<ExportObject> findByRuntimeId(int runtimeId);
}
