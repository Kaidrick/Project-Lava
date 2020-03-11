package moe.ofs.backend.services;

import moe.ofs.backend.object.FlyableUnit;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class BoxOfFlyableUnitServiceImpl implements BoxOfFlyableUnitService {

    private final FlyableUnitService flyableUnitService;

    public BoxOfFlyableUnitServiceImpl(FlyableUnitService flyableUnitService) {
        this.flyableUnitService = flyableUnitService;
    }

    @Override
    public Optional<Integer> getGroupIdByName(String groupName) {
        return flyableUnitService.findGroupIdByName(groupName);
    }

    @Override
    public Set<FlyableUnit> getAll() {
        return flyableUnitService.findAll();
    }
}
