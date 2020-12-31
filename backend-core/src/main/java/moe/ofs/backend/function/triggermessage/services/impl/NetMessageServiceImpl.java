package moe.ofs.backend.function.triggermessage.services.impl;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.function.triggermessage.model.TriggerMessage;
import moe.ofs.backend.function.triggermessage.services.NetMessageService;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NetMessageServiceImpl implements NetMessageService {
    private String playersToNetIdString(List<PlayerInfo> players) {
        return players.stream()
                .map(PlayerInfo::getNetId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public void sendNetMessageToAll(String message) {
        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "message/send_net_message_to_all.lua", message);
    }

    @Override
    public void sendNetMessageToAll(TriggerMessage message) {
        sendNetMessageToAll(message.getMessage());
    }

    @Override
    public void sendNetMessageForPlayer(TriggerMessage message, PlayerInfo player) {
        if (player == null) {  // send message to all players
            sendNetMessageToAll(message);
            return;
        }

        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "message/send_net_message_by_net_id.lua",
                message.getMessage(), player.getNetId());
    }

    @Override
    public void sendNetMessageForPlayers(TriggerMessage message, List<PlayerInfo> players) {
        sendNetMessageForPlayers(message.getMessage(), players);
    }

    @Override
    public void sendNetMessageForPlayer(String messageContent, PlayerInfo playerInfo) {
        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "message/send_net_message_by_net_id.lua",
                messageContent, playerInfo.getNetId());
    }

    @Override
    public void sendNetMessageForPlayers(String messageContent, List<PlayerInfo> players) {
        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "message/send_net_message_by_net_id_list.lua",
                playersToNetIdString(players), messageContent);
    }

    @Override
    public void whisperNetMessageToPlayer(String messageContent, PlayerInfo from, PlayerInfo to) {
        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "message/send_net_message_whisper_to_net_id.lua",
                messageContent, from.getNetId(), to.getNetId());
    }

    @Override
    public void whisperNetMessageToPlayers(String messageContent, PlayerInfo from, List<PlayerInfo> players) {
        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "message/send_net_message_whisper_to_net_id_list.lua",
                messageContent, from.getNetId(), playersToNetIdString(players));
    }
}
