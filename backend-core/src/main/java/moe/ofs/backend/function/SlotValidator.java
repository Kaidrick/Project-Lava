package moe.ofs.backend.function;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.handlers.BackgroundTaskRestartObservable;
import moe.ofs.backend.handlers.ControlPanelShutdownObservable;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to check and validate a net player's request to enter a particular slot.
 * It will check ban list and other customized criteria before moving the player to this slot.
 *
 * When a player tries to enter a slot
 */

@Slf4j
@Component
public class SlotValidator {

    private MissionStartObservable missionStartObservable;
    private BackgroundTaskRestartObservable backgroundTaskRestartObservable;
    private ControlPanelShutdownObservable controlPanelShutdownObservable;

    private final PlayerInfoService playerInfoService;
    private final Gson gson = new Gson();

    private ScheduledExecutorService slotEntryPullExecutorService;

    public SlotValidator(PlayerInfoService playerInfoService) {
        this.playerInfoService = playerInfoService;
    }

    @PostConstruct
    public void init() {
        missionStartObservable = this::setUp;
        missionStartObservable.register();

        backgroundTaskRestartObservable = this::tearDown;
        backgroundTaskRestartObservable.register();

        controlPanelShutdownObservable = this::tearDown;
        controlPanelShutdownObservable.register();

        log.info("Initialized SlotValidator");
    }

    public void setUp() {
        new ServerDataRequest(LuaScripts.load("slotchange/player_change_slot_hook.lua")).send();

        Runnable getSlotEntryRequest = () -> {
            new ServerDataRequest(RequestToServer.State.DEBUG,
                    LuaScripts.load("slotchange/pull_slot_entry_request.lua"))
                    .addProcessable(s -> {
                        Type entryRequestListType = new TypeToken<List<SlotEntryRequest>>() {}.getType();
                        List<SlotEntryRequest> entryRequestList = gson.fromJson(s, entryRequestListType);

                        entryRequestList.forEach(System.out::println);
                        entryRequestList.forEach(slotEntryRequest -> {
                            Optional<PlayerInfo> optional =
                                    playerInfoService.findByNetId(slotEntryRequest.getNetId());

                            // run criteria here?
                            if(optional.isPresent()) {
                                PlayerInfo playerInfo = optional.get();
                                new ServerDataRequest(RequestToServer.State.DEBUG,
                                        LuaScripts.loadAndPrepare("slotchange/force_player_slot.lua",
                                                slotEntryRequest.getNetId(),
                                                slotEntryRequest.getSide(),
                                                slotEntryRequest.getSlotId())).send();
                            }
                        });

                    }).send();
        };

        slotEntryPullExecutorService = Executors.newSingleThreadScheduledExecutor();
        slotEntryPullExecutorService.scheduleWithFixedDelay(getSlotEntryRequest,
                0, 100, TimeUnit.MILLISECONDS);

    }

    public void tearDown() {
        if(slotEntryPullExecutorService != null) {
            slotEntryPullExecutorService.shutdown();
        }
        new ServerDataRequest(RequestToServer.State.DEBUG, "slot_validator.clean_request()").send();
    }
}
