package moe.ofs.backend.discipline.service.impl;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.discipline.model.PlayerTryConnectRecord;
import moe.ofs.backend.function.mizdb.PersistentKeyValueInjectionBootstrap;
import moe.ofs.backend.handlers.starter.LuaScriptStarter;
import moe.ofs.backend.handlers.starter.model.ScriptInjectionTask;
import moe.ofs.backend.hookinterceptor.AbstractHookInterceptorProcessService;
import moe.ofs.backend.hookinterceptor.HookInterceptorDefinition;
import moe.ofs.backend.hookinterceptor.HookInterceptorProcessService;
import moe.ofs.backend.hookinterceptor.HookType;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.services.mizdb.SimpleKeyValueStorage;
import moe.ofs.backend.util.LuaInteract;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class NewPlayerConnectionValidationServiceImpl
        extends AbstractHookInterceptorProcessService<PlayerTryConnectRecord, HookInterceptorDefinition>
        implements HookInterceptorProcessService<PlayerTryConnectRecord, HookInterceptorDefinition>, LuaScriptStarter {

    private final PlayerInfoService playerInfoService;
    private final SimpleKeyValueStorage<String> connectionValidatorStorage;

    public NewPlayerConnectionValidationServiceImpl(PlayerInfoService playerInfoService) {
        this.playerInfoService = playerInfoService;

        connectionValidatorStorage =
                new SimpleKeyValueStorage<>("lava-default-player-connection-validation-hook-kw-pair",
                        LuaQueryEnv.SERVER_CONTROL);
    }

    @Override
    public ScriptInjectionTask injectScript() {
        return ScriptInjectionTask.builder()
                .scriptIdentName("PlayerConnectionValidationService")
                .initializrClass(getClass())
                .dependencyInitializrClass(PersistentKeyValueInjectionBootstrap.class)
                .inject(() -> {
                    createHook(getClass().getName(), HookType.ON_PLAYER_TRY_CONNECT);

                    HookInterceptorDefinition hookInterceptorDefinition =
                            HookInterceptorDefinition.builder()
                                    .name("lava-default-player-connection-validation-hook-interceptor")
                                    .storage(connectionValidatorStorage)
                                    // FIXME: looks weird; how does it even work?
                                    .predicateFunction(HookInterceptorProcessService.FUNCTION_RETURN_ORIGINAL_ARGS)
                                    .decisionMappingFunction("" +
                                            "function(" + HookType.ON_PLAYER_TRY_CONNECT.getFunctionArgsString(null) + ") " +
                                            "   local data = { " +
                                            "       ipaddr = addr, " +
                                            "       playerName = name, " +
                                            "       ucid = ucid " +
                                            "   } " +
                                            "   return data " +
                                            "end")
                                    .build();

                    return addDefinition(hookInterceptorDefinition);
                })
                .injectionDoneCallback(aBoolean -> {
                    if (aBoolean) log.info("Hook Interceptor Initialized: {}", getName());
                    else log.error("Failed to initiate hook interceptor: {}", getName());
                })
                .build();
    }

    @Scheduled(fixedDelay = 100L)
    @LuaInteract
    public void gather() throws IOException {
        poll(PlayerTryConnectRecord.class).stream()
                .peek(hookProcessEntity ->
                        playerInfoService.findByNetId(hookProcessEntity.getNetId())
                                .ifPresent(hookProcessEntity::setPlayer))
                .forEach(System.out::println);
    }
}
