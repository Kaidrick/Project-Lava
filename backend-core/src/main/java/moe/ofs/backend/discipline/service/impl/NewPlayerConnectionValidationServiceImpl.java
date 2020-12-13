package moe.ofs.backend.discipline.service.impl;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.hookinterceptor.*;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.services.mizdb.SimpleKeyValueStorage;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
@Slf4j
public class NewPlayerConnectionValidationServiceImpl
        extends AbstractHookInterceptorProcessService<HookProcessEntity, HookInterceptorDefinition>
        implements HookInterceptorProcessService<HookProcessEntity, HookInterceptorDefinition> {

    private final PlayerInfoService playerInfoService;
    private final SimpleKeyValueStorage<String> connectionValidatorStorage;

    public NewPlayerConnectionValidationServiceImpl(PlayerInfoService playerInfoService) {
        this.playerInfoService = playerInfoService;

        connectionValidatorStorage =
                new SimpleKeyValueStorage<>("lava-default-player-connection-validation-hook-kw-pair",
                        LuaQueryEnv.SERVER_CONTROL);
    }

    @PostConstruct
    public void init() {
        MissionStartObservable missionStartObservable = theaterName -> {
            createHook(getClass().getName(), HookType.ON_PLAYER_TRY_CONNECT);

            HookInterceptorDefinition hookInterceptorDefinition =
                    HookInterceptorDefinition.builder()
                            .name("lava-default-player-connection-validation-hook-interceptor")
                            .storage(connectionValidatorStorage)
                            .predicateFunction(HookInterceptorProcessService.FUNCTION_RETURN_ORIGINAL_ARGS)
                            .build();

            addDefinition(hookInterceptorDefinition);

            log.info("Hook Interceptor Initialized: {}", getName());
        };
        missionStartObservable.register();
    }

    @Scheduled(fixedDelay = 100L)

    // FIXME: use annotation based identifier so that the function will only run when connection is established
    public void gather() throws IOException {
        poll().stream()
                .peek(hookProcessEntity ->
                        playerInfoService.findByNetId(hookProcessEntity.getNetId())
                                .ifPresent(hookProcessEntity::setPlayer))
                .forEach(System.out::println);
    }

}
