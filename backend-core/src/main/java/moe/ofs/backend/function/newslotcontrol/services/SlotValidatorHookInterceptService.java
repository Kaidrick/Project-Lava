package moe.ofs.backend.function.newslotcontrol.services;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.function.newslotcontrol.model.SlotChangeData;
import moe.ofs.backend.function.newslotcontrol.model.SlotChangeInterceptor;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.hookinterceptor.HookInterceptorDefinition;
import moe.ofs.backend.hookinterceptor.HookInterceptorProcessService;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.services.mizdb.SimpleKeyValueStorage;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import moe.ofs.backend.util.lua.LuaQueryState;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

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
public class SlotValidatorHookInterceptService implements SlotValidatorService,
        HookInterceptorProcessService<SlotChangeData, HookInterceptorDefinition> {

    private final PlayerInfoService playerInfoService;

    private final SimpleKeyValueStorage<String> storage;

    public SlotValidatorHookInterceptService(PlayerInfoService playerInfoService) {
        this.playerInfoService = playerInfoService;

        storage = new SimpleKeyValueStorage<>(
                "lava-slot-change-interceptor-kw-storage", LuaQueryEnv.SERVER_CONTROL
        );
    }

    @PostConstruct
    public void init() {
        MissionStartObservable missionStartObservable = s -> {
//            LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
//                    "slotchange/new/player_slot_record_hook.lua", getClass().getName());

            SlotChangeInterceptor interceptor =
                    new SlotChangeInterceptor("lava-slot-change-interceptor",
                            "function(...) return true end", storage);

            addDefinition(interceptor);

            log.info("Initiating Slot Validator Injection Service");
        };
        missionStartObservable.register();
    }

    @Override
    public void createHook(String name) {
        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "generic_hook_interceptor/create_hook.lua", name);
    }

    /**
     * Pull record data from dcs, logs only
     * @return
     * @throws IOException
     */
    @Override
    public List<SlotChangeData> poll() throws IOException {

        // TODO: associate in player info service?
        return LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "slotchange/new/fetch_player_slot_request.lua").getAsListFor(SlotChangeData.class);
    }

    @Override
    public void addDefinition(HookInterceptorDefinition definition) {
        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "generic_hook_interceptor/add_definition.lua",
                getClass().getName(), definition.getName(),
                definition.getHookType().getFunctionName(), storage.getRepositoryName(),
                definition.getPredicateFunction());
    }

    @Override
    public void removeDefinition(HookInterceptorDefinition definition) {

    }
}
