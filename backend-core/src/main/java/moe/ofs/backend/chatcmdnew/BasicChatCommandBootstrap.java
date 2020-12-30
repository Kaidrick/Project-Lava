package moe.ofs.backend.chatcmdnew;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.chatcmdnew.model.ChatCommandDefinition;
import moe.ofs.backend.chatcmdnew.services.ChatCommandSetManageService;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.function.triggermessage.model.TriggerMessage;
import moe.ofs.backend.function.triggermessage.services.TriggerMessageService;
import moe.ofs.backend.services.PlayerInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BasicChatCommandBootstrap {
    private final ChatCommandSetManageService commandSetManageService;
    private final TriggerMessageService triggerMessageService;
    private final PlayerInfoService playerInfoService;

    public BasicChatCommandBootstrap(ChatCommandSetManageService commandSetManageService,
                                     TriggerMessageService triggerMessageService, PlayerInfoService playerInfoService) {
        this.commandSetManageService = commandSetManageService;
        this.triggerMessageService = triggerMessageService;
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
                            TriggerMessage message = triggerMessageService.getTriggerMessageTemplate()
                                    .setReceiverGroupId(0)
                                    .setMessage(playerInfo.toString())
                                    .build();
                            triggerMessageService.sendNetMessageForPlayer(message, playerInfo);
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
                            TriggerMessage message = triggerMessageService.getTriggerMessageTemplate()
                                    .setReceiverGroupId(0)
                                    .setDuration(20)
                                    .setClearView(false)
                                    .setMessage(
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
                                bound = Double.valueOf(chatCommandProcessEntity.getMessage().substring(
                                        chatCommandProcessEntity.getKeyword().length())).intValue();
                            }

                            int diceRoll = (int) Math.ceil(Math.random() * bound);

                            TriggerMessage message = triggerMessageService.getTriggerMessageTemplate()
                                    .setReceiverGroupId(0)
                                    .setDuration(20)
                                    .setClearView(false)
                                    .setMessage(
                                            playerInfo.getName() + " rolled " + diceRoll + " (1-" + bound + ")."
                                    )
                                    .build();

                            triggerMessageService.sendNetMessageForPlayers(message,
                                    playerInfoService.findAll().stream()
                                            .filter(p -> p.getNetId() != 1)
                                            .collect(Collectors.toList()));
                        })
                        .build()
        );
    }
}
