package moe.ofs.backend.services;

import moe.ofs.backend.box.BoxOfFlyableUnit;
import moe.ofs.backend.object.FlyableUnit;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class BoxOfFlyableUnitServiceImpl implements BoxOfFlyableUnitService {

    // very badly encapsulated, TODO -> properly implement repository
    private final Map<String, FlyableUnit> box = BoxOfFlyableUnit.box;

    @Override
    public Optional<Integer> getGroupIdByName(String groupName) {
        return BoxOfFlyableUnit.getGroupIdByName(groupName);
    }

    @Override
    public Map<String, FlyableUnit> getAll() {
        return box;
    }
}
