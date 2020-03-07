package moe.ofs.backend.services;

import moe.ofs.backend.object.FlyableUnit;

import java.util.Optional;

public interface FlyableUnitService extends CrudService<FlyableUnit>, StaticService {

    Optional<FlyableUnit> findByUnitName(String name);

    Optional<FlyableUnit> findByUnitId(String idString);

    Optional<FlyableUnit> findByUnitId(Long id);

    Optional<Integer> findGroupIdByName(String name);

    void dispose();
}
