package moe.ofs.backend.function.triggermessage.controller;

import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.function.triggermessage.model.MessageType;
import moe.ofs.backend.function.triggermessage.model.TriggerMessage;
import moe.ofs.backend.function.triggermessage.model.TriggerMessageRequest;
import moe.ofs.backend.function.triggermessage.services.TriggerMessageService;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("trigger")
public class TriggerMessageController {

    private final TriggerMessageService triggerMessageService;
    private final PlayerInfoService playerInfoService;

    public TriggerMessageController(TriggerMessageService triggerMessageService,
                                    PlayerInfoService playerInfoService) {
        this.triggerMessageService = triggerMessageService;
        this.playerInfoService = playerInfoService;
    }

    @PostMapping("message")
    public List<String> send(@RequestBody TriggerMessageRequest request) {
        TriggerMessage triggerMessage = triggerMessageService.getTriggerMessageTemplate()
                .setMessage(request.getMessage())
                .setClearView(request.isClearView())
                .setDuration(request.getDuration())
                .setReceiverGroupId(0)
                .build();

        if (request.isUseTriggerMessageWhenPossible()) {
            switch (request.getType()) {
                case ALL:
                    List<PlayerInfo> players = new ArrayList<>(playerInfoService.findAll());
                    triggerMessageService.sendTriggerMessageForPlayers(triggerMessage, players);

                    return players.stream().map(PlayerInfo::getUcid).collect(Collectors.toList());
                case RED:
                    List<PlayerInfo> reds = request.getUcidList().stream()
                            .map(playerInfoService::findByUcid)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .filter(playerInfo -> playerInfo.getSide() == 1)
                            .collect(Collectors.toList());
                    triggerMessageService.sendTriggerMessageForPlayers(triggerMessage, reds);

                    return reds.stream().map(PlayerInfo::getUcid).collect(Collectors.toList());
                case BLUE:
                    List<PlayerInfo> blues = request.getUcidList().stream()
                            .map(playerInfoService::findByUcid)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .filter(playerInfo -> playerInfo.getSide() == 2)
                            .collect(Collectors.toList());
                    triggerMessageService.sendTriggerMessageForPlayers(triggerMessage, blues);

                    return blues.stream().map(PlayerInfo::getUcid).collect(Collectors.toList());
                default:
                    break;
            }
        } else {
            switch (request.getType()) {
                case ALL:
                    List<PlayerInfo> players = new ArrayList<>(playerInfoService.findAll());
                    triggerMessageService.sendNetMessageForPlayers(triggerMessage, players);

                    return players.stream().map(PlayerInfo::getUcid).collect(Collectors.toList());
                case RED:
                    List<PlayerInfo> reds = request.getUcidList().stream()
                            .map(playerInfoService::findByUcid)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .filter(playerInfo -> playerInfo.getSide() == 1)
                            .collect(Collectors.toList());
                    triggerMessageService.sendNetMessageForPlayers(triggerMessage, reds);

                    return reds.stream().map(PlayerInfo::getUcid).collect(Collectors.toList());
                case BLUE:
                    List<PlayerInfo> blues = request.getUcidList().stream()
                            .map(playerInfoService::findByUcid)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .filter(playerInfo -> playerInfo.getSide() == 2)
                            .collect(Collectors.toList());
                    triggerMessageService.sendNetMessageForPlayers(triggerMessage, blues);

                    return blues.stream().map(PlayerInfo::getUcid).collect(Collectors.toList());
                default:
                    break;
            }
        }



        return null;
    }

}
