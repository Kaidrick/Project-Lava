package moe.ofs.backend.discipline.service.impl;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.connector.lua.LuaQueryEnv;
import moe.ofs.backend.discipline.model.PlayerTryConnectRecord;
import moe.ofs.backend.discipline.service.GlobalConnectionBlockService;
import moe.ofs.backend.discipline.service.PlayerConnectionValidationService;
import moe.ofs.backend.domain.connector.handlers.scripts.LuaScriptStarter;
import moe.ofs.backend.domain.connector.handlers.scripts.ScriptInjectionTask;
import moe.ofs.backend.function.mizdb.services.impl.LuaStorageInitServiceImpl;
import moe.ofs.backend.hookinterceptor.AbstractHookInterceptorProcessService;
import moe.ofs.backend.hookinterceptor.HookInterceptorDefinition;
import moe.ofs.backend.hookinterceptor.HookType;
import moe.ofs.backend.services.mizdb.SimpleKeyValueStorage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
@Slf4j
public class PlayerConnectionValidationServiceImpl
        extends AbstractHookInterceptorProcessService<PlayerTryConnectRecord, HookInterceptorDefinition>
        implements PlayerConnectionValidationService,
        LuaScriptStarter, GlobalConnectionBlockService {

    private final SimpleKeyValueStorage<String> connectionValidatorStorage;
    private final SimpleKeyValueStorage<Object> globalConnectionBlockStorage;

    public PlayerConnectionValidationServiceImpl() {

        connectionValidatorStorage =
                new SimpleKeyValueStorage<>("lava-default-player-connection-validation-hook-kw-pair",
                        LuaQueryEnv.SERVER_CONTROL);

        globalConnectionBlockStorage =
                new SimpleKeyValueStorage<>("lava-default-player-connection-block-hook-kw-pair",
                        LuaQueryEnv.SERVER_CONTROL);
    }

    @PostConstruct
    public void populateProcessors() {
        addProcessor("console log processor", playerTryConnectRecord ->
                log.info("Player {}<{}> tries to connect from {}",
                        playerTryConnectRecord.getPlayerName(),
                        playerTryConnectRecord.getUcid(),
                        playerTryConnectRecord.getIpaddr()));
    }

    @Override
    public ScriptInjectionTask injectScript() {
        return ScriptInjectionTask.builder()
                .scriptIdentName("PlayerConnectionValidationService")
                .initializrClass(getClass())
                .dependencyInitializrClass(LuaStorageInitServiceImpl.class)
                .inject(() -> {
                    boolean hooked = createHook(getClass().getName(), HookType.ON_PLAYER_TRY_CONNECT);

                    HookInterceptorDefinition hookInterceptorDefinition =
                            HookInterceptorDefinition.builder()
                                    .name("lava-default-player-connection-validation-hook-interceptor")
                                    .storage(connectionValidatorStorage)
                                    .predicateFunction("" +
                                            "function(" + HookType.ON_PLAYER_TRY_CONNECT.getFunctionArgsString("store") + ") " +
                                            "   if store:get(ucid) then " +
                                            "       return false, store:get(ucid) " +
                                            "   end " +
                                            "end")
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
    public void gather() throws IOException {
        gather(PlayerTryConnectRecord.class);
//        poll(PlayerTryConnectRecord.class).stream()
//                .peek(hookProcessEntity ->
//                        playerInfoService.findByNetId(hookProcessEntity.getNetId())
//                                .ifPresent(hookProcessEntity::setPlayer))
//                .forEach(playerTryConnectRecord -> // TODO: send to message queue?
//                        log.info("Player {}<{}> tries to connect from {}",
//                                playerTryConnectRecord.getPlayerName(),
//                                playerTryConnectRecord.getUcid(),
//                                playerTryConnectRecord.getIpaddr()));
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

    @Override
    public void blockPlayerUcid(String ucid) {
        connectionValidatorStorage.save(ucid, "你被ban了！ You are banned！");
    }
    @Override
    public void blockPlayerUcid(String ucid, String reason) {
        connectionValidatorStorage.save(ucid, reason);
    }

    @Override
    public void unblockPlayerUcid(String ucid) {
        connectionValidatorStorage.delete(ucid);
    }
}
