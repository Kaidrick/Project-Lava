package moe.ofs.backend.discipline.service.impl;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.discipline.model.PlayerTryConnectRecord;
import moe.ofs.backend.discipline.service.GlobalConnectionBlockService;
import moe.ofs.backend.handlers.starter.LuaScriptStarter;
import moe.ofs.backend.handlers.starter.model.ScriptInjectionTask;
import moe.ofs.backend.hookinterceptor.AbstractHookInterceptorProcessService;
import moe.ofs.backend.hookinterceptor.HookInterceptorDefinition;
import moe.ofs.backend.hookinterceptor.HookInterceptorProcessService;
import moe.ofs.backend.hookinterceptor.HookType;
import moe.ofs.backend.function.mizdb.services.impl.LuaStorageInitServiceImpl;
import moe.ofs.backend.services.PlayerDataService;
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
        implements HookInterceptorProcessService<PlayerTryConnectRecord, HookInterceptorDefinition>,
        LuaScriptStarter, GlobalConnectionBlockService {

    private final PlayerDataService playerInfoService;
    private final SimpleKeyValueStorage<String> connectionValidatorStorage;
    private final SimpleKeyValueStorage<Object> globalConnectionBlockStorage;

    public NewPlayerConnectionValidationServiceImpl(PlayerDataService playerInfoService) {
        this.playerInfoService = playerInfoService;

        connectionValidatorStorage =
                new SimpleKeyValueStorage<>("lava-default-player-connection-validation-hook-kw-pair",
                        LuaQueryEnv.SERVER_CONTROL);

        globalConnectionBlockStorage =
                new SimpleKeyValueStorage<>("lava-default-player-connection-block-hook-kw-pair",
                        LuaQueryEnv.SERVER_CONTROL);
    }

    @Override
    public ScriptInjectionTask injectScript() {
        return ScriptInjectionTask.builder()
                .scriptIdentName("PlayerConnectionValidationService")
                .initializrClass(getClass())
                .dependencyInitializrClass(LuaStorageInitServiceImpl.class)
                .inject(() -> {
                    boolean hooked = createHook(getClass().getName(), HookType.ON_PLAYER_TRY_CONNECT);

                    log.info("{} Hooked? {}", getName(), hooked);

                    HookInterceptorDefinition hookInterceptorDefinition =
                            HookInterceptorDefinition.builder()
                                    .name("lava-default-player-connection-validation-hook-interceptor")
                                    .storage(connectionValidatorStorage)
//                                    .predicateFunction("" +
//                                            "function(" + HookType.ON_PLAYER_TRY_CONNECT.getFunctionArgsString("store") + ") " +
//                                            "   net.log('enter sandman') " +
//                                            "end")
                                    .argPostProcessFunction("" +
                                            "function(" + HookType.ON_PLAYER_TRY_CONNECT.getFunctionArgsString("store") + ") " +
                                            "   local data = { " +
                                            "       ipaddr = addr, " +
                                            "       playerName = name, " +
                                            "       ucid = ucid " +
                                            "   } " +
                                            "   return data " +
                                            "end")
                                    .build();

                    HookInterceptorDefinition globalBlockDefinition = HookInterceptorDefinition.builder()
                            .name("lava-default-player-connection-block-hook-interceptor")
                            .storage(globalConnectionBlockStorage)
                            .predicateFunction("" +
                                    "function(store, ...) " +
                                    "   net.log('global block function test enter') " +
                                    "   if store and store:get('isBlocked') then " +
                                    "       return false, store:get('blockReason') " +
                                    "   end " +
                                    "end ")
                            .argPostProcessFunction("" +
                                    "function(" + HookType.ON_PLAYER_TRY_CONNECT.getFunctionArgsString("store") + ") " +
                                    "   local data = { " +
                                    "       ipaddr = addr, " +
                                    "       playerName = name, " +
                                    "       ucid = ucid " +
                                    "   } " +
                                    "   return data " +
                                    "end")
                            .build();

                    return hooked &&
                            addDefinition(hookInterceptorDefinition) &&
                            addDefinition(globalBlockDefinition);
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
                .forEach(playerTryConnectRecord -> // TODO: send to message queue?
                        log.info("Player {}<{}> tries to connect from {}",
                                playerTryConnectRecord.getPlayerName(),
                                playerTryConnectRecord.getUcid(),
                                playerTryConnectRecord.getIpaddr()));
    }

    @Override
    public void block(String reason) {
        globalConnectionBlockStorage.save("isBlocked", true);
        globalConnectionBlockStorage.save("blockReason", reason);
    }

    @Override
    public void release() {
        globalConnectionBlockStorage.save("isBlocked", false);
        globalConnectionBlockStorage.delete("blockReason");
    }
}
