package moe.ofs.backend.function.newslotcontrol.services;

import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.function.newslotcontrol.model.SlotChangeData;
import moe.ofs.backend.hookinterceptor.AbstractHookInterceptorProcessService;
import moe.ofs.backend.request.DataRequest;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
public class SlotValidatorInjectionService extends AbstractHookInterceptorProcessService<SlotChangeData>
        implements SlotValidatorService {

    public SlotValidatorInjectionService(RequestTransmissionService requestTransmissionService,
                                         PlayerInfoService playerInfoService) {
        super(requestTransmissionService, playerInfoService);
    }

    @PostConstruct
    public void init() {
//        requestTransmissionService.send(
//                new ServerDataRequest(RequestToServer.State.DEBUG,
//                        LuaScripts.loadAndPrepare("slotchange/new/player_slot_record_hook.lua",
//                                getClass().getName())));
    }

    /**
     * Pull record data from dcs, logs only
     * @return
     * @throws IOException
     */
    @Override
    public List<SlotChangeData> poll() throws IOException {
        Type type = TypeToken.getParameterized(ArrayList.class, SlotChangeData.class).getType();
        return ((ServerDataRequest) requestTransmissionService.send(
                new ServerDataRequest(LuaQueryEnv.SERVER_CONTROL,
                        LuaScripts.loadAndPrepare("slotchange/new/fetch_player_slot_request.lua"))
        )).getAs(type);
    }
}
