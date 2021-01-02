package moe.ofs.backend.chatcmd.services;

import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.chatcmdnew.model.ChatCommandDefinition;
import moe.ofs.backend.chatcmdnew.model.ScanStrategy;
import moe.ofs.backend.domain.ChatCommand;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.util.LuaInteract;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
//@Service
public class ChatCommandProcessServiceImpl implements ChatCommandProcessService {
    private final List<Processable> processors = new ArrayList<>();

    // TODO: keyword should be unique; use set instead
    private final List<ChatCommandDefinition> definitions = new ArrayList<>();

    private final RequestTransmissionService requestTransmissionService;
    private final PlayerInfoService playerInfoService;

    public ChatCommandProcessServiceImpl(RequestTransmissionService requestTransmissionService, PlayerInfoService playerInfoService) {
        this.requestTransmissionService = requestTransmissionService;
        this.playerInfoService = playerInfoService;
    }

//    @PostConstruct
    public void setup() {
        MissionStartObservable missionStartObservable = s -> {
            LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                    "chat_command_processor/chat_command_processor.lua");

            log.info("Setting up chat command processor");

            String batchKwString = definitions.stream()
                    .map(definition -> String.format("\"%s\"", definition.getKeyword()))
                    .collect(Collectors.joining(", "));

            requestTransmissionService
                    .send(new ServerDataRequest(LuaQueryEnv.SERVER_CONTROL,
                            LuaScripts.loadAndPrepare("chat_command_processor/add_command_keyword_batch.lua",
                                    batchKwString)));

        };
        missionStartObservable.register();

        addDefinition(ChatCommandDefinition.builder()
                .consumer(chatCommand -> System.out.println("chatCommand = " + chatCommand))
                .name("test chat command intercept")
                .keyword("/test")
                .strategy(ScanStrategy.STARTS_WITH)
                .build());
    }

    @Scheduled(fixedDelay = 200L)
    @LuaInteract
    public void gather() {
        poll().forEach(this::analysis);
    }

    @Override
    public List<ChatCommand> poll() {
        Type type = TypeToken.getParameterized(ArrayList.class, ChatCommand.class).getType();
        return ((ServerDataRequest) requestTransmissionService
                .send(new ServerDataRequest(LuaQueryEnv.SERVER_CONTROL,
                    LuaScripts.load("chat_command_processor/fetch_commands.lua"))))
                .getAs(type);
    }

    @Override
    public void analysis(ChatCommand command) {
        // which player is initiating this command
        Optional<PlayerInfo> optional = playerInfoService.findByNetId(command.getNetId());
        optional.ifPresent(playerInfo -> command.setPlayer(optional.get()));

//        definitions.stream()
//                .filter(definition -> definition.getKeyword().equals(command.getKeyword()))
//                .forEach(definition -> definition.getConsumer().accept(command));
    }

    @Override
    public void addProcessor(Processable processable) {
        processors.add(processable);
    }

    @Override
    public void addProcessors(List<Processable> processors) {
        this.processors.addAll(processors);
    }

    @Override
    public void removeProcessor(Processable processor) {
        this.processors.remove(processor);
    }

    @Override
    public void removeProcessors(List<Processable> processors) {
        this.processors.removeAll(processors);
    }

    @Override
    public ChatCommandDefinition addDefinition(ChatCommandDefinition definition) {
        definitions.add(definition);
//        requestTransmissionService
//                .send(new ServerDataRequest(RequestToServer.State.DEBUG,
//                        LuaScripts.loadAndPrepare("chat_command_processor/add_command_keyword.lua",
//                                definition.getKeyword())));

        return definition;
    }
}
