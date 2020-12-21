package moe.ofs.backend.function.triggermessage.services.impl;

import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.function.triggermessage.model.TriggerMessage;
import moe.ofs.backend.function.triggermessage.services.TriggerMessageService;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.FlyableUnitService;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TriggerMessageServiceImpl implements TriggerMessageService {

    private static final String triggerMessageByGroupId = LuaScripts.load("send_message_by_group_id.lua");

    private final RequestTransmissionService requestTransmissionService;
    private final FlyableUnitService flyableUnitService;

    public TriggerMessageServiceImpl(RequestTransmissionService requestTransmissionService, FlyableUnitService flyableUnitService) {
        this.requestTransmissionService = requestTransmissionService;
        this.flyableUnitService = flyableUnitService;
    }

    @Override
    public TriggerMessage.TriggerMessageBuilder getTriggerMessageTemplate() {
        return new TriggerMessage.TriggerMessageBuilder();
    }

    @Override
    public void sendTriggerMessage(TriggerMessage triggerMessage) {
        String preparedString = String.format(triggerMessageByGroupId,
                triggerMessage.getReceiverGroupId(), triggerMessage.getMessage(),
                triggerMessage.getDuration(), triggerMessage.isClearView());
        System.out.println(preparedString);
        requestTransmissionService.send(new ServerExecRequest(preparedString));
    }

    @Override
    public void sendTriggerMessageForPlayer(TriggerMessage message, PlayerInfo player) {
        String preparedString = String.format(triggerMessageByGroupId,
                flyableUnitService.findByUnitId(player.getSlot()), message.getMessage(),
                message.getDuration(), message.isClearView());
        System.out.println(preparedString);
        requestTransmissionService.send(new ServerExecRequest(preparedString));
    }

    @Override
    public void sendTriggerMessageForPlayers(TriggerMessage message, List<PlayerInfo> players) {
        // concat string
    }
}
