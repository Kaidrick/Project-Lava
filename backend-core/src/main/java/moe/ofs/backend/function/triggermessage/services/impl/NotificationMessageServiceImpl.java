package moe.ofs.backend.function.triggermessage.services.impl;

import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.domain.admin.message.MessageFallback;
import moe.ofs.backend.domain.admin.message.TriggerMessage;
import moe.ofs.backend.function.triggermessage.services.NetMessageService;
import moe.ofs.backend.function.triggermessage.services.NotificationMessageService;
import moe.ofs.backend.function.triggermessage.services.TriggerMessageService;
import moe.ofs.backend.dataservice.exportobject.ExportObjectService;
import moe.ofs.backend.dataservice.slotunit.FlyableUnitService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationMessageServiceImpl implements NotificationMessageService {

    private final FlyableUnitService flyableUnitService;
    private final ExportObjectService exportObjectService;

    private final TriggerMessageService triggerMessageService;
    private final NetMessageService netMessageService;

    public NotificationMessageServiceImpl(FlyableUnitService flyableUnitService,
                                          ExportObjectService exportObjectService, TriggerMessageService triggerMessageService, NetMessageService netMessageService) {
        this.flyableUnitService = flyableUnitService;
        this.exportObjectService = exportObjectService;
        this.triggerMessageService = triggerMessageService;
        this.netMessageService = netMessageService;
    }

    @Override
    public void notifyPlayer(TriggerMessage message, PlayerInfo playerInfo) {
        notifyPlayer(message, playerInfo, MessageFallback.NET_MESSAGE_AS_FALLBACK);
    }

    @Override
    public void notifyPlayer(TriggerMessage message, PlayerInfo playerInfo, MessageFallback fallback) {
//        Optional<FlyableUnit> flyableUnitOptional = flyableUnitService.findByUnitId(playerInfo.getSlot());
//        if (flyableUnitOptional.isPresent()) {
//            Optional<ExportObject> exportObjectOptional =
//                    exportObjectService.findByUnitName(flyableUnitOptional.get().getUnit_name());
//
//            if (exportObjectOptional.isPresent()) {
//                exportObjectService.findAll().stream().filter(exportObject -> exportObject.)
//            }
//        } else {
//            // check fallback option
//        }
//
//        boolean unitMatched = flyableUnitService.findByUnitId(playerInfo.getSlot()).isPresent();
//
//        if (unitMatched) {
//            exportObjectService.findByUnitName()
//        }
//
//        // check if player has a matching group id for unit and if the matching unit exists in the mission
//        flyableUnitService.findByUnitId(playerInfo.getSlot()).ifPresent(
//                flyableUnit -> exportObjectService.findByUnitName(flyableUnit.getUnit_name()).ifPresent()
//        );
    }

    @Override
    public void notifyPlayers(TriggerMessage message, List<PlayerInfo> players) {

    }

    @Override
    public void notifyPlayers(TriggerMessage message, List<PlayerInfo> players, MessageFallback fallback) {

    }
}
