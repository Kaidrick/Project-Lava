package moe.ofs.backend.function.slotcontrol;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.handlers.BackgroundTaskRestartObservable;
import moe.ofs.backend.handlers.ControlPanelShutdownObservable;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.request.DataRequest;
import moe.ofs.backend.request.server.ServerActionRequest;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This class is used to check and validate a net player's request to enter a particular slot.
 * It will check ban list and other customized criteria before moving the player to this slot.
 *
 * When a player tries to enter a slot,the slot change event in dcs is intercepted in hook environment,
 * and a request change info table is queued into a table from which the data will be pulled to backend.
 * The backend then determines whether the player has met the requirement to enter said slot.
 * If the player paases the validation, the backend actively sends a message to dcs lua server to
 * move force player into said slot, thus completing the slot change process.
 *
 * A hand-off should be set to switch of the intercept flag in the dcs lua server. If the backend failed to
 * send a heartbeat signal to dcs lua server within 
 *
 */

@Slf4j
@Component
public class SlotValidator {

    private MissionStartObservable missionStartObservable;
    private BackgroundTaskRestartObservable backgroundTaskRestartObservable;
    private ControlPanelShutdownObservable controlPanelShutdownObservable;

    private final RequestTransmissionService requestTransmissionService;

    private final Gson gson = new Gson();

    private List<PlayerSlotControl> playerSlotControls = new ArrayList<>();

    private ScheduledExecutorService slotEntryPullExecutorService;

    public SlotValidator(RequestTransmissionService requestTransmissionService) {
        this.requestTransmissionService = requestTransmissionService;
    }

    public List<PlayerSlotControl> getPlayerSlotControls() {
        return playerSlotControls;
    }

    public void setPlayerSlotControls(List<PlayerSlotControl> playerSlotControls) {
        this.playerSlotControls = playerSlotControls;
    }

    // setUp on mission start
    @PostConstruct
    public void init() {
        missionStartObservable = theaterName -> setUp();
        missionStartObservable.register();

        backgroundTaskRestartObservable = this::tearDown;
        backgroundTaskRestartObservable.register();

        controlPanelShutdownObservable = this::tearDown;
        controlPanelShutdownObservable.register();

        log.info("Slot Validator Initialized");
    }

    public void setUp() {
        requestTransmissionService.send(new ServerDataRequest(LuaQueryEnv.SERVER_CONTROL,
                LuaScripts.load("slotchange/player_change_slot_hook.lua")));

        log.info("Setting up playable slot validator");

        Runnable getSlotEntryRequest = () -> {
            requestTransmissionService.send(
                    new ServerDataRequest(LuaQueryEnv.SERVER_CONTROL,
                            LuaScripts.load("slotchange/pull_slot_entry_request.lua"))
                            .addProcessable(s -> {
                                Type entryRequestListType = new TypeToken<List<SlotChangeRequest>>() {}.getType();
                                List<SlotChangeRequest> entryRequestList = gson.fromJson(s, entryRequestListType);

                                entryRequestList.forEach(slotChangeRequest -> {

                                    // when a player enters the server, his/her data is always updated to the db
                                    // the loading time is usually pretty long, say more than 10 seconds
                                    // therefore, when the player initiates a request to enter a slot,
                                    // the player info should always be available

                                    // now, what if the player made a request to enter a slot,
                                    // and before server reacted, the player left server or enters another slot?
                                    // if the player left server, not a problem
                                    // if the player enters another slot, the server will still move the player
                                    // previous slot selected. it's always a delayed action.

                                    // run criteria here
                                    if(playerSlotControls.isEmpty()) {
                                        // if no criteria, pass the check immediately
                                        requestTransmissionService.send(
                                                new ServerDataRequest(LuaQueryEnv.SERVER_CONTROL,
                                                        LuaScripts.loadAndPrepare("slotchange/force_player_slot.lua",
                                                                slotChangeRequest.getNetId(),
                                                                slotChangeRequest.getSide(),
                                                                slotChangeRequest.getSlotId()))
                                        );
                                    } else {

                                        List<PlayerSlotControl> list = playerSlotControls.stream()
                                                .filter(control -> !control.validate(slotChangeRequest).isAllowed())
                                                .collect(Collectors.toList());

                                        if(list.isEmpty()) {
                                            requestTransmissionService.send(
                                                    new ServerDataRequest(LuaQueryEnv.SERVER_CONTROL,
                                                            LuaScripts.loadAndPrepare("slotchange/force_player_slot.lua",
                                                                    slotChangeRequest.getNetId(),
                                                                    slotChangeRequest.getSide(),
                                                                    slotChangeRequest.getSlotId()))
                                            );
                                        } else {
                                            // send notice to player about why request is denied
                                        }
                                    }
                                });

                            })
            );
        };
        slotEntryPullExecutorService = Executors.newSingleThreadScheduledExecutor();
        slotEntryPullExecutorService.scheduleWithFixedDelay(getSlotEntryRequest,
                0, 200, TimeUnit.MILLISECONDS);

    }

    /**
     * also need to handoff to dcs
     */
    public void tearDown() {
        if(slotEntryPullExecutorService != null) {
            slotEntryPullExecutorService.shutdownNow();
        }

        requestTransmissionService.send(new ServerActionRequest(LuaQueryEnv.SERVER_CONTROL,
                LuaScripts.load("slotchange/clean_request_table.lua")));
    }
}
