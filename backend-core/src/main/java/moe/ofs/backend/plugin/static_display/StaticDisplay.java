package moe.ofs.backend.plugin.static_display;

import moe.ofs.backend.Plugin;
import moe.ofs.backend.PluginClassLoader;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.function.SlotChangeRequest;
import moe.ofs.backend.function.SlotChangeResult;
import moe.ofs.backend.function.SlotValidator;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.handlers.PlayerLeaveServerObservable;
import moe.ofs.backend.logmanager.Logger;
import moe.ofs.backend.object.FlyableUnit;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.services.FlyableUnitService;
import moe.ofs.backend.services.ParkingInfoService;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This addon class implements the functionality to place a static object matching the type and livery of
 * a flyable aircraft when the aircraft is idle (slot not occupied by a player)

 * TODO -> normal unit cannot replace static object because it occupies a parking in game
 */

@Component
public class StaticDisplay implements Plugin {

    // name
    public final String name = "Static Aircraft Display";
    public final String desc = "Display a flyable static aircraft";

    private boolean isLoaded;

    // handlers
    MissionStartObservable missionStartObservable;
    PlayerLeaveServerObservable playerLeaveServerObservable;

    private static final List<String> excludedTypeList = Arrays.asList("SA342M", "SA342L",
            "SA342Mistral", "SA342Minigun");

    // slot id as string, runtimeId as string
    private static final Map<String, String> mapSlotStaticId = new HashMap<>();

    // predefined replacement map, used to force spawn with an old lockon models if an matching entry exists
    private static final Map<String, String> replacement = new HashMap<>();

    static {
        replacement.put("FA-18C_hornet", "F/A-18C");
        replacement.put("F-14B", "F-14A");
    }

    private static final String luaStringAddStatic = LuaScripts.load("add_static_object.lua");
    private static final String luaStringRemoveObject = LuaScripts.load("remove_object_by_runtime_id.lua");

    private final FlyableUnitService flyableUnitService;
    private final ParkingInfoService parkingInfoService;

    private final SlotValidator slotValidator;

    @Autowired
    public StaticDisplay(FlyableUnitService flyableUnitService, ParkingInfoService parkingInfoService, SlotValidator slotValidator) {
        this.flyableUnitService = flyableUnitService;
        this.parkingInfoService = parkingInfoService;
        this.slotValidator = slotValidator;
    }

    @PostConstruct
    public void init() {
        System.out.println("Static Display plugin bean constructed..register");
        Plugin.super.init();
        PluginClassLoader.loadedPluginSet.add(this);
    }

    /**
     * Register to PlayerChangeSlot
     * get player current slot and check if this slot has a matching unit with same UnitID in flyable unit box
     * then remove the static object from the env
     *
     * Register to PlayerLeaveServer as well
     * when player leaves server, get the current slot of the player, check for matching unit
     */
    @Override
    public void register() {
        slotValidator.getPlayerSlotControls().add(this::switchStaticDisplay);

        missionStartObservable = this::initStaticDisplay;
        missionStartObservable.register();

        playerLeaveServerObservable = this::respawnOnPlayerLeaveServer;
        playerLeaveServerObservable.register();

        isLoaded = true;

        writeConfiguration("enabled", "true");
    }

    @Override
    public void unregister() {
        playerLeaveServerObservable.unregister();
        missionStartObservable.unregister();
        playerLeaveServerObservable.unregister();

        cleanStaticDisplay();

        isLoaded = false;

        writeConfiguration("enabled", "false");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }


    // if connection is not established, wait for establishment and than init
    // if connection is already established, init immediately?
    public void initStaticDisplay() {
        // for each playable, spawn static object if TakeOffGround or TakeOffParking

        System.out.println("initStaticDisplay called");
        flyableUnitService.findAll().forEach(this::spawnControl);
    }

    public void cleanStaticDisplay() {
        // find flyable unit and pass to despawn control method
        Logger.log("Disabling Static Display...removing existing static objects");
        mapSlotStaticId.keySet().forEach(id -> flyableUnitService.findByUnitId(id).ifPresent(this::despawnControl));
    }

    private void spawnControl(FlyableUnit flyableUnit) {
        String type;
        String rawType = flyableUnit.getType();
        type = replacement.getOrDefault(rawType, rawType);

        if(!excludedTypeList.contains(type)) {
            String startType = flyableUnit.getStart_type();

            double heading;
            if (startType.equals("TakeOffGround")) {
                heading = flyableUnit.getHeading();
            } else if (startType.equals("TakeOffParking")) {
                heading = parkingInfoService.getParking(flyableUnit.getAirdromeId(), flyableUnit.getParking()).get()
                        .getInitialHeading();
            } else {
                return;
            }

            String p = String.format(luaStringAddStatic,
                    "_StaticDisplay_" + flyableUnit.getUnit_name(), type,
                    flyableUnit.getX(), flyableUnit.getY(), flyableUnit.getLivery_id(),
                    flyableUnit.getOnboard_num(), heading, flyableUnit.getCountry_id());

            new ServerDataRequest(p)
                    .addProcessable(s -> mapSlotStaticId.put(String.valueOf(flyableUnit.getUnit_id()), s))
                    .addProcessable(s -> Logger.addon(
                            String.format("Static Object [%s] spawned for %s with livery [%s]",
                                    s, flyableUnit.getUnit_name(), flyableUnit.getLivery_id())
                    )).send();
        }  // else no spawn
    }

    private void despawnControl(FlyableUnit flyableUnit) {
        String runtimeId = mapSlotStaticId.get(String.valueOf(flyableUnit.getUnit_id()));
        new ServerDataRequest(String.format(luaStringRemoveObject, runtimeId))
                .addProcessable(s -> Logger.addon(runtimeId + " -> static object removed"))
                .send();
    }

    private void respawnOnPlayerLeaveServer(PlayerInfo playerInfo) {
        // find previous slot
        flyableUnitService.findByUnitId(playerInfo.getSlot()).ifPresent(this::spawnControl);
    }

    private SlotChangeResult switchStaticDisplay(SlotChangeRequest request) {
        flyableUnitService.findByUnitId(request.getCurrentSlotId()).ifPresent(this::spawnControl);
        flyableUnitService.findByUnitId(request.getSlotId()).ifPresent(this::despawnControl);

        return new SlotChangeResult(true);
    }
}
