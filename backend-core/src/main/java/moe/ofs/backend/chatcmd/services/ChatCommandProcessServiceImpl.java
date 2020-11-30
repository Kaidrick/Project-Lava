package moe.ofs.backend.chatcmd.services;

import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.BackgroundTask;
import moe.ofs.backend.domain.ChatCommand;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.message.OperationPhase;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ChatCommandProcessServiceImpl implements ChatCommandProcessService {
    private final List<Processable> processors = new ArrayList<>();

    private final RequestTransmissionService requestTransmissionService;
    private final PlayerInfoService playerInfoService;

    public ChatCommandProcessServiceImpl(RequestTransmissionService requestTransmissionService, PlayerInfoService playerInfoService) {
        this.requestTransmissionService = requestTransmissionService;
        this.playerInfoService = playerInfoService;
    }

    @PostConstruct
    public void setup() {
        MissionStartObservable missionStartObservable = s -> {
            requestTransmissionService.send(new ServerDataRequest(RequestToServer.State.DEBUG,
                    LuaScripts.load("chat_command_processor/chat_command_processor.lua")));

            log.info("Setting up chat command processor");
        };
        missionStartObservable.register();
    }

    @Scheduled(fixedDelay = 200L)
    public void gather() {
        if (BackgroundTask.getCurrentTask().getPhase().equals(OperationPhase.RUNNING)) {
            poll().forEach(this::analysis);
        }
    }

    @Override
    public List<ChatCommand> poll() {
        Type type = TypeToken.getParameterized(ArrayList.class, ChatCommand.class).getType();
        return ((ServerDataRequest) requestTransmissionService
                .send(new ServerDataRequest(RequestToServer.State.DEBUG,
                    LuaScripts.load("chat_command_processor/fetch_commands.lua")))).getAs(type);
    }

    @Override
    public void analysis(ChatCommand command) {
        Optional<PlayerInfo> optional = playerInfoService.findByNetId(command.getNetId());
        optional.ifPresent(playerInfo -> {
            command.setPlayer(optional.get());
            processors.forEach(processable -> processable.process(command));
        });
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
}
