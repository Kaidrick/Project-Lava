package moe.ofs.backend.services;

import moe.ofs.backend.object.FlyableUnit;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface BoxOfFlyableUnitService {
    Optional<Integer> getGroupIdByName(String groupName);

    Set<FlyableUnit> getAll();
}
