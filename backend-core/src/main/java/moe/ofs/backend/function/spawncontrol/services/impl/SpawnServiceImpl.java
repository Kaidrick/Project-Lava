package moe.ofs.backend.function.spawncontrol.services.impl;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.dcs.poll.ExportObject;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.function.spawncontrol.services.SpawnService;
import moe.ofs.backend.object.FlyableUnit;
import moe.ofs.backend.dataservice.exportobject.ExportObjectService;
import moe.ofs.backend.dataservice.slotunit.FlyableUnitService;
import moe.ofs.backend.dataservice.player.PlayerInfoService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class SpawnServiceImpl implements SpawnService {
    private final PlayerInfoService playerInfoService;
    private final ExportObjectService exportObjectService;
    private final FlyableUnitService flyableUnitService;

    public SpawnServiceImpl(PlayerInfoService playerInfoService, ExportObjectService exportObjectService,
                            FlyableUnitService flyableUnitService) {
        this.playerInfoService = playerInfoService;
        this.exportObjectService = exportObjectService;
        this.flyableUnitService = flyableUnitService;
    }

    @Override
    public boolean isPlayerSpawned(PlayerInfo playerInfo) {
        Optional<FlyableUnit> flyableUnitOptional =
                flyableUnitService.findByUnitId(playerInfo.getSlot());

        if (flyableUnitOptional.isPresent()) {
            return exportObjectService.findByUnitName(playerInfo.getName()).isPresent();
            // TODO: need to check whether player name can be used to link export object
            // TODO: if not, use mission env to check
        }

        return false;
    }

    @Override
    public Optional<PlayerInfo> getUnitOccupant(ExportObject exportObject) {
        if (!exportObject.getStatus().get("Human")) {
            return Optional.empty();
        }

        Optional<FlyableUnit> flyableUnitOptional =
                flyableUnitService.findByUnitName(exportObject.getUnitName());

        if (flyableUnitOptional.isPresent()) {
            return playerInfoService.findBySlot(String.valueOf(flyableUnitOptional.get().getUnit_id()));
        }

        return Optional.empty();
    }
}
