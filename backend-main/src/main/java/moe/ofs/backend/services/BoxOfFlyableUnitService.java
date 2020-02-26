package moe.ofs.backend.services;

import moe.ofs.backend.object.FlyableUnit;

import java.util.Map;
import java.util.Optional;

public interface BoxOfFlyableUnitService {
    Optional<Integer> getGroupIdByName(String groupName);

    Map<String, FlyableUnit> getAll();
}
