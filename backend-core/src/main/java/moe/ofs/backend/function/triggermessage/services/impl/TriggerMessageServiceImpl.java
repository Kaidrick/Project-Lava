package moe.ofs.backend.function.triggermessage.services.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.function.triggermessage.PlayerNotActiveException;
import moe.ofs.backend.function.triggermessage.model.TriggerMessage;
import moe.ofs.backend.function.triggermessage.services.TriggerMessageService;
import moe.ofs.backend.object.FlyableUnit;
import moe.ofs.backend.services.FlyableUnitService;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TriggerMessageServiceImpl implements TriggerMessageService {

    private final FlyableUnitService flyableUnitService;
    private final PlayerInfoService playerInfoService;

    public TriggerMessageServiceImpl(FlyableUnitService flyableUnitService,
                                     PlayerInfoService playerInfoService) {
        this.flyableUnitService = flyableUnitService;
        this.playerInfoService = playerInfoService;
    }

    private String playersToGroupIdString(List<PlayerInfo> players) {
        return players.stream().map(player -> flyableUnitService.findByUnitId(player.getSlot()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(FlyableUnit::getGroup_id)
                .map(String::valueOf).collect(Collectors.joining(","));
    }

    @Override
    public void sendTriggerMessage(@NonNull TriggerMessage triggerMessage) {
        if (triggerMessage.getReceiverGroupId() == 0) {  // no receive group id specified; send to all players
            sendTriggerMessageForPlayers(triggerMessage, new ArrayList<>(playerInfoService.findAll()));
            return;
        }

        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "message/send_message_by_group_id.lua",
                triggerMessage.getReceiverGroupId(), triggerMessage.getMessage(),
                triggerMessage.getDuration(), triggerMessage.isClearView());
    }

    @Override
    public void sendTriggerMessageForPlayer(@NonNull TriggerMessage message, PlayerInfo player) {
        if (player == null) {
            sendTriggerMessage(message);
            return;
        }
        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "message/send_message_by_group_id.lua",
                flyableUnitService.findByUnitId(player.getSlot()).map(FlyableUnit::getGroup_id).orElseThrow(
                        PlayerNotActiveException::new),
                message.getMessage(), message.getDuration(), message.isClearView());
    }

    @Override
    public void sendTriggerMessageForPlayers(@NonNull TriggerMessage message, List<PlayerInfo> players) {
        if (players == null || players.isEmpty()) {
            sendTriggerMessage(message);
            return;
        }

        LuaScripts.requestWithFile(LuaQueryEnv.MISSION_SCRIPTING,
                "message/send_message_by_group_id_list.lua",
                playersToGroupIdString(players),
                message.getMessage(), message.getDuration(),  message.isClearView());
    }
}