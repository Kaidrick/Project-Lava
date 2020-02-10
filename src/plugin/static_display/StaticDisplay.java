package plugin.static_display;

import core.Logger;
import core.LuaScripts;
import core.MissionStartObservable;
import core.Plugin;
import core.box.BoxOfFlyableUnit;
import core.box.BoxOfParking;
import core.object.FlyableUnit;
import core.object.PlayerInfo;
import core.request.server.ServerDataRequest;
import core.request.server.handler.PlayerLeaveServerObservable;
import core.request.server.handler.PlayerSlotChangeObservable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This addon class implements the functionality to place a static object matching the type and livery of
 * a flyable aircraft when the aircraft is idle (slot not occupied by a player)

 * TODO -> normal unit cannot replace static object because it occupies a parking in game
 * TODO -> therefore the only option to make it work for cold start flyable unit
 * TODO -> is to manually gather heading information for each parking position.
 * TODO -> get parking position from
 */
public class StaticDisplay implements Plugin {
    private static final String luaStringAddStatic = LuaScripts.load("add_static_object.lua");
    private static final String luaStringRemoveObject = LuaScripts.load("remove_object_by_runtime_id.lua");

    /**
     * Register to PlayerChangeSlot
     * get player current slot and check if this slot has a matching unit with same UnitID in flyable unit box
     * then remove the static object from the env
     *
     * Register to PlayerLeaveServer as well
     * when player leaves server, get the current slot of the player, check for matching unit
     */
    public void register() {
        PlayerSlotChangeObservable playerSlotChangeObservable = this::switchStaticDisplay;
        playerSlotChangeObservable.register();

        MissionStartObservable missionStartObservable = StaticDisplay::initStaticDisplay;
        missionStartObservable.register();

        PlayerLeaveServerObservable playerLeaveServerObservable = this::respawnOnPlayerLeaveServer;
        playerLeaveServerObservable.register();
    }


    public static void initStaticDisplay() {
        // for each playable, spawn static object if TakeOffGround or TakeOffParking
        BoxOfFlyableUnit.box.values().forEach(StaticDisplay::spawnControl);
    }

    private static final List<String> excludedTypeList = Arrays.asList("SA342M", "SA342L",
            "SA342Mistral", "SA342Minigun");

    // slot id as string, runtimeId as string
    private static final Map<String, String> mapSlotStaticId = new HashMap<>();
    private static final Map<String, String> replacement = new HashMap<>();
    static {
        replacement.put("FA-18C_hornet", "F/A-18C");
        replacement.put("F-14B", "F-14A");
    }

    private static void spawnControl(FlyableUnit flyableUnit) {
        String type;
        String rawType = flyableUnit.getType();
        type = replacement.getOrDefault(rawType, rawType);

        if(!excludedTypeList.contains(type)) {
            String startType = flyableUnit.getStart_type();

            double heading;
            if (startType.equals("TakeOffGround")) {
                heading = flyableUnit.getHeading();
            } else if (startType.equals("TakeOffParking")) {
                heading = BoxOfParking.get(flyableUnit.getAirdromeId(), flyableUnit.getParking())
                        .getInitialHeading();
            } else return;
            String p = String.format(luaStringAddStatic,
                    "_StaticDisplay_" + flyableUnit.getUnit_name(), type,
                    flyableUnit.getX(), flyableUnit.getY(), flyableUnit.getLivery_id(),
                    flyableUnit.getOnboard_num(), heading, flyableUnit.getCountry_id());

            new ServerDataRequest(p)
                    .addProcessable(s -> mapSlotStaticId.put(String.valueOf(flyableUnit.getUnit_id()), s))
                    .addProcessable(s -> Logger.log(s + " -> static object spawned"))
                    .send();
        }  // else no spawn
    }

    private static void despawnControl(FlyableUnit flyableUnit) {
        String runtimeId = mapSlotStaticId.get(String.valueOf(flyableUnit.getUnit_id()));
        new ServerDataRequest(String.format(luaStringRemoveObject, runtimeId))
                .addProcessable(s -> Logger.log(runtimeId + " -> static object removed"))
                .send();
    }

    private void respawnOnPlayerLeaveServer(PlayerInfo playerInfo) {
        // find previous slot
        BoxOfFlyableUnit.box.values().stream()
                .filter(f -> String.valueOf(f.getUnit_id()).equals(playerInfo.getSlot()))
                .findAny().ifPresent(StaticDisplay::spawnControl);
    }

    // TODO --> redundant code, see above

    private void switchStaticDisplay(PlayerInfo previous, PlayerInfo current) {
        BoxOfFlyableUnit.box.values().stream()
                .filter(f -> String.valueOf(f.getUnit_id()).equals(previous.getSlot()))
                .findAny().ifPresent(StaticDisplay::spawnControl);

        BoxOfFlyableUnit.box.values().stream()
                .filter(f -> String.valueOf(f.getUnit_id()).equals(current.getSlot()))
                .findAny().ifPresent(StaticDisplay::despawnControl);
    }
}
