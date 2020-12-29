package moe.ofs.backend.function.newslotcontrol.services;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.BackgroundTask;
import moe.ofs.backend.function.mizdb.PersistentKeyValueInjectionBootstrap;
import moe.ofs.backend.handlers.starter.LuaScriptStarter;
import moe.ofs.backend.handlers.starter.model.ScriptInjectionTask;
import moe.ofs.backend.hookinterceptor.*;
import moe.ofs.backend.message.OperationPhase;
import moe.ofs.backend.services.LuaStorageInitServiceImpl;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.services.mizdb.SimpleKeyValueStorage;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import moe.ofs.backend.util.lua.LuaQueryState;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * When a player tries to enter a slot, the callback is executed.
 * In the callback, check what static object is linked to this slot id.
 *
 * When static object is spawn, the static object id is stored into a map for unit id and static object runtime id.
 *
 * If this happens at mission start, save all this map into lua hook env.
 * If this happens at player despawn, save a particular entry into lua hook env.
 *
 * When a try change slot is run through the callback, the callback will check if injected map in kw storage repository
 * has a corresponding runtime id for requested slot id (unit id). If there is, destroy the runtime id object
 * and let go.
 *
 * TODO: use onPlayerChangeSlot(id) to destroy the aircraft properly rather than onPlayerTryChangeSlot
 *
 * However, if the slot id is observer/commander/jtac/admin or multi-seat aircraft second/third seat, skip the
 * static object destroy process.
 *
 * If a try change slot request is rejected after static object despawn, then it is necessary to confirm slot change
 * has been made by checking whether player slot change occurs. If not, respawn the static object.
 *
 */

@Service
@Slf4j
@LuaQueryState(LuaQueryEnv.SERVER_CONTROL)
public class SlotValidatorHookInterceptService
        extends AbstractHookInterceptorProcessService<HookProcessEntity, HookInterceptorDefinition>
        implements SlotValidatorService, HookInterceptorProcessService<HookProcessEntity, HookInterceptorDefinition>,
        LuaScriptStarter {

    private final PlayerInfoService playerInfoService;

    private final SimpleKeyValueStorage<String> storage;

    public SlotValidatorHookInterceptService(PlayerInfoService playerInfoService) {
        this.playerInfoService = playerInfoService;

        storage = new SimpleKeyValueStorage<>(
                "lava-slot-change-interceptor-kw-storage", LuaQueryEnv.SERVER_CONTROL
        );
    }

    @Override
    public ScriptInjectionTask injectScript() {
        return ScriptInjectionTask.builder()
                .scriptIdentName("SlotValidatorHookInterceptService")
                .initializrClass(getClass())
                .dependencyInitializrClass(LuaStorageInitServiceImpl.class)
                .inject(() -> {
                    boolean hooked = createHook(getClass().getName(), HookType.ON_PLAYER_TRY_CHANGE_SLOT);

                    HookInterceptorDefinition interceptor =
                            HookInterceptorDefinition.builder()
                                    .name("lava-slot-change-interceptor")
                                    .predicateFunction(HookInterceptorProcessService.FUNCTION_RETURN_ORIGINAL_ARGS)
                                    .storage(storage)
                                    .build();
//                            new HookInterceptorDefinition("lava-slot-change-interceptor",
//                                    HookInterceptorProcessService.FUNCTION_RETURN_ORIGINAL_ARGS, storage,
//                                    null, null, null);

                    return hooked && addDefinition(interceptor);
                })
                .injectionDoneCallback(aBoolean -> {
                    if (aBoolean) log.info("Hook Interceptor Initialized: {}", getName());
                    else log.error("Failed to initiate Slot Validator Injection Service");
                })
                .build();
    }

    @Scheduled(fixedDelay = 1000L)
    public void gather() throws IOException {
        if (BackgroundTask.getCurrentTask().getPhase().equals(OperationPhase.RUNNING)) {
            poll().stream().peek(entity ->
                    playerInfoService.findByNetId(entity.getNetId()).ifPresent(entity::setPlayer))
                    .forEach(System.out::println);
        }

    }
}
