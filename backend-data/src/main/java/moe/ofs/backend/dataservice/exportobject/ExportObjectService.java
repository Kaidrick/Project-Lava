package moe.ofs.backend.dataservice.exportobject;

import moe.ofs.backend.common.CrudService;
import moe.ofs.backend.common.MissionPersistenceRepository;
import moe.ofs.backend.common.UpdatableService;
import moe.ofs.backend.domain.dcs.poll.ExportObject;

import java.util.Optional;

public interface ExportObjectService extends UpdatableService<ExportObject>, CrudService<ExportObject>,
        MissionPersistenceRepository {

    void dispose();

    Optional<ExportObject> findByUnitName(String unitName);

    Optional<ExportObject> findByRuntimeId(Long runtimeId);

    Optional<ExportObject> findByRuntimeId(String runtimeId);

}
