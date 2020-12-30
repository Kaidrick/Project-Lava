package moe.ofs.backend.function.triggermessage.services.impl;

import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.function.triggermessage.model.TriggerMessage;
import moe.ofs.backend.function.triggermessage.services.TriggerMessageService;
import moe.ofs.backend.object.FlyableUnit;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.FlyableUnitService;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TriggerMessageServiceImpl implements TriggerMessageService {

    private static final String triggerMessageByGroupId = LuaScripts.load("message/send_message_by_group_id.lua");

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
                flyableUnitService.findByUnitId(player.getSlot()).map(FlyableUnit::getGroup_id).orElseThrow(
                        () -> new RuntimeException("Unable to match player with an existing group id; " +
                                "the player is probably not active in mission.")
                ),
                message.getMessage(),
                message.getDuration(), message.isClearView());
        System.out.println(preparedString);
        requestTransmissionService.send(new ServerExecRequest(preparedString));
    }

    @Override
    public void sendTriggerMessageForPlayers(TriggerMessage message, List<PlayerInfo> players) {
        System.out.println("players = " + players);
        // concat string
        String groupIdString = players.stream().map(player -> flyableUnitService.findByUnitId(player.getSlot()))
                .peek(System.out::println).filter(Optional::isPresent)
                .map(Optional::get)
                .map(FlyableUnit::getGroup_id)
                .map(String::valueOf).collect(Collectors.joining(","));

        System.out.println("groupIdString = " + groupIdString);

        LuaScripts.requestWithFile(LuaQueryEnv.MISSION_SCRIPTING,
                "message/send_message_by_group_id_list.lua",
                groupIdString, message.getMessage(), message.getDuration(),  message.isClearView());
    }

    @Override
    public void sendNetMessageForPlayer(TriggerMessage message, PlayerInfo player) {
        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "message/send_net_message_by_net_id.lua", message.getMessage(), player.getNetId());
    }

    @Override
    public void sendNetMessageForPlayers(TriggerMessage message, List<PlayerInfo> players) {
        String listString = players.stream()
                .map(PlayerInfo::getNetId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        System.out.println("listString = " + listString);
        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "message/send_net_message_by_net_id_list.lua", listString, message.getMessage());
    }
}
