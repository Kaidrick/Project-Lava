package moe.ofs.backend.chatcmdnew;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.chatcmdnew.model.ChatCommandDefinition;
import moe.ofs.backend.chatcmdnew.services.ChatCommandSetManageService;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.function.triggermessage.model.TriggerMessage;
import moe.ofs.backend.function.triggermessage.services.NetMessageService;
import moe.ofs.backend.function.triggermessage.services.TriggerMessageService;
import moe.ofs.backend.dataservice.PlayerInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BasicChatCommandBootstrap {
    private final ChatCommandSetManageService commandSetManageService;
    private final TriggerMessageService triggerMessageService;
    private final NetMessageService netMessageService;
    private final PlayerInfoService playerInfoService;

    public BasicChatCommandBootstrap(ChatCommandSetManageService commandSetManageService,
                                     TriggerMessageService triggerMessageService,
                                     NetMessageService netMessageService, PlayerInfoService playerInfoService) {
        this.commandSetManageService = commandSetManageService;
        this.triggerMessageService = triggerMessageService;
        this.netMessageService = netMessageService;
        this.playerInfoService = playerInfoService;
    }

    /**
     * add some funny looking chat commands
     */
    @PostConstruct
    public void init() {
        commandSetManageService.addCommandDefinition(
                ChatCommandDefinition.builder()
                        .name("query-player-self-info")
                        .keyword("/whoami")
                        .description("Echo back player info")
                        .consumer(chatCommandProcessEntity -> {
                            PlayerInfo playerInfo = chatCommandProcessEntity.getPlayer();
                            TriggerMessage message = TriggerMessage.builder()
                                    .receiverGroupId(0)
                                    .message(playerInfo.toString())
                                    .build();
                            netMessageService.sendNetMessageForPlayer(message, playerInfo);
                        })
                        .build()
        );

        commandSetManageService.addCommandDefinition(
                ChatCommandDefinition.builder()
                        .name("chat-command-help-info")
                        .keyword("/help")
                        .description("Show all available chat commands")
                        .consumer(chatCommandProcessEntity -> {
                            PlayerInfo playerInfo = chatCommandProcessEntity.getPlayer();
                            TriggerMessage message = TriggerMessage.builder()
                                    .receiverGroupId(0)
                                    .duration(20)
                                    .clearView(false)
                                    .message(
                                            "All available commands are as follows: \n" +
                                            commandSetManageService.findAllCommandDefinition().stream()
                                                    .map(d -> d.getKeyword() + ": " + d.getDescription())
                                                    .collect(Collectors.joining("\n"))
                                    )
                                    .build();
                            triggerMessageService.sendTriggerMessageForPlayer(message, playerInfo);
                        })
                        .build()
        );

        commandSetManageService.addCommandDefinition(
                ChatCommandDefinition.builder()
                        .name("misc-roll-dice")
                        .keyword("/roll")
                        .description("Do you feel lucky, punk?")
                        .consumer(chatCommandProcessEntity -> {
                            PlayerInfo playerInfo = chatCommandProcessEntity.getPlayer();
                            int bound = 100;

                            if (!chatCommandProcessEntity.getMessage().equals(chatCommandProcessEntity.getKeyword())) {
                                try {
                                    bound = Double.valueOf(chatCommandProcessEntity.getMessage().substring(
                                            chatCommandProcessEntity.getKeyword().length())).intValue();
                                } catch (NumberFormatException ignored) {}
                            }

                            int diceRoll = Math.abs((int) Math.ceil(Math.random() * bound));

                            TriggerMessage message = TriggerMessage.builder()
                                    .receiverGroupId(0)
                                    .duration(20)
                                    .clearView(false)
                                    .message(
                                            playerInfo.getName() + " rolled " + diceRoll + " (1-" + bound + ")."
                                    )
                                    .build();

                            netMessageService.sendNetMessageForPlayers(message,
                                    new ArrayList<>(playerInfoService.findAll(true)));
                        })
                        .build()
        );
    }
}
