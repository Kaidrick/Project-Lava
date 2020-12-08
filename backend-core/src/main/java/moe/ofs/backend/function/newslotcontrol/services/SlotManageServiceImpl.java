package moe.ofs.backend.function.newslotcontrol.services;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.object.FlyableUnit;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.ExportObjectService;
import moe.ofs.backend.services.FlyableUnitService;
import moe.ofs.backend.services.PlayerInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
@Slf4j
public class SlotManageServiceImpl implements SlotManageService {

    private final RequestTransmissionService requestTransmissionService;
    private final PlayerInfoService playerInfoService;
    private final FlyableUnitService flyableUnitService;
    private final ExportObjectService exportObjectService;

    public SlotManageServiceImpl(RequestTransmissionService requestTransmissionService,
                                 PlayerInfoService playerInfoService,
                                 FlyableUnitService flyableUnitService, ExportObjectService exportObjectService) {
        this.requestTransmissionService = requestTransmissionService;
        this.playerInfoService = playerInfoService;
        this.flyableUnitService = flyableUnitService;
        this.exportObjectService = exportObjectService;
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
