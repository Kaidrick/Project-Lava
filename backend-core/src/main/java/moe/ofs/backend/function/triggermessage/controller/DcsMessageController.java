package moe.ofs.backend.function.triggermessage.controller;

import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.function.triggermessage.model.TriggerMessage;
import moe.ofs.backend.function.triggermessage.model.TriggerMessageRequest;
import moe.ofs.backend.function.triggermessage.services.NetMessageService;
import moe.ofs.backend.function.triggermessage.services.TriggerMessageService;
import moe.ofs.backend.services.PlayerInfoService;
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
public class DcsMessageController {

    private final TriggerMessageService triggerMessageService;
    private final NetMessageService netMessageService;
    private final PlayerInfoService playerInfoService;

    public DcsMessageController(TriggerMessageService triggerMessageService,
                                NetMessageService netMessageService, PlayerInfoService playerInfoService) {
        this.triggerMessageService = triggerMessageService;
        this.netMessageService = netMessageService;
        this.playerInfoService = playerInfoService;
    }

    @PostMapping("message")
    public List<String> send(@RequestBody TriggerMessageRequest request) {
        TriggerMessage triggerMessage = TriggerMessage.builder()
                .message(request.getMessage())
                .clearView(request.isClearView())
                .duration(request.getDuration())
                .receiverGroupId(0)
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
                    netMessageService.sendNetMessageForPlayers(triggerMessage, players);

                    return players.stream().map(PlayerInfo::getUcid).collect(Collectors.toList());
                case RED:
                    List<PlayerInfo> reds = request.getUcidList().stream()
                            .map(playerInfoService::findByUcid)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .filter(playerInfo -> playerInfo.getSide() == 1)
                            .collect(Collectors.toList());
                    netMessageService.sendNetMessageForPlayers(triggerMessage, reds);

                    return reds.stream().map(PlayerInfo::getUcid).collect(Collectors.toList());
                case BLUE:
                    List<PlayerInfo> blues = request.getUcidList().stream()
                            .map(playerInfoService::findByUcid)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .filter(playerInfo -> playerInfo.getSide() == 2)
                            .collect(Collectors.toList());
                    netMessageService.sendNetMessageForPlayers(triggerMessage, blues);

                    return blues.stream().map(PlayerInfo::getUcid).collect(Collectors.toList());
                default:
                    break;
            }
        }



        return null;
    }

}
