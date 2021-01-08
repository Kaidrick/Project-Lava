package moe.ofs.backend.function.newslotcontrol.services;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.dcs.poll.ExportObject;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.object.FlyableUnit;
import moe.ofs.backend.dataservice.ExportObjectService;
import moe.ofs.backend.dataservice.FlyableUnitService;
import moe.ofs.backend.dataservice.PlayerInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
@Slf4j
public class SlotManageServiceImpl implements SlotManageService {

    private final PlayerInfoService playerInfoService;
    private final FlyableUnitService flyableUnitService;
    private final ExportObjectService exportObjectService;

    private final SlotValidatorService slotValidatorService;

    public SlotManageServiceImpl(PlayerInfoService playerInfoService,
                                 FlyableUnitService flyableUnitService, ExportObjectService exportObjectService, SlotValidatorService slotValidatorService) {
        this.playerInfoService = playerInfoService;
        this.flyableUnitService = flyableUnitService;
        this.exportObjectService = exportObjectService;

        this.slotValidatorService = slotValidatorService;
    }

    @PostConstruct
    public void init() {

    }

    @Override
    public void forceSlot(PlayerInfo playerInfo, String slotId) {
        // force_player_slot.lua
    }

    @Override
    public Optional<ExportObject> getSlotExportObject(String slotId) {
        Optional<FlyableUnit> optionalFlyableUnit = flyableUnitService.findByUnitId(slotId);
        if (optionalFlyableUnit.isPresent()) {
            return exportObjectService.findByUnitName(optionalFlyableUnit.get().getUnit_name());
        }
        return Optional.empty();
    }

    @Override
    public void lockSlot(String slotId) {
        // add a validator so that this particular slot is blocked
    }

    @Override
    public void releaseSlot(String slotId) {
        // remove the validator from lua predicates
    }

    @Override
    public void emptySlot(String slotId) {
        // move whoever in the slot to observer slot
        playerInfoService.findBySlot(slotId)
                .ifPresent(playerInfo -> forceSlot(playerInfo, slotId));
    }

    @Override
    public void emptyAllSlots() {
        // move all player to observer slot
    }
}
