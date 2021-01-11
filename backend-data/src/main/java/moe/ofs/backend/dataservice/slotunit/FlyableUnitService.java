package moe.ofs.backend.dataservice.slotunit;

import moe.ofs.backend.common.CrudService;
import moe.ofs.backend.common.MissionPersistenceRepository;
import moe.ofs.backend.common.StaticService;
import moe.ofs.backend.object.FlyableUnit;

import java.util.Optional;

public interface FlyableUnitService extends CrudService<FlyableUnit>, StaticService, MissionPersistenceRepository {

    Optional<FlyableUnit> findByUnitName(String name);

    Optional<FlyableUnit> findByUnitId(String idString);

    Optional<FlyableUnit> findByUnitId(Long id);

    Optional<Integer> findGroupIdByName(String name);

    void dispose();
}
