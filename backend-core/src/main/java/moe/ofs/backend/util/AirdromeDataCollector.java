package moe.ofs.backend.util;

import com.google.gson.Gson;
import moe.ofs.backend.connector.lua.LuaQueryEnv;
import moe.ofs.backend.object.Group;
import moe.ofs.backend.object.ParkingInfo;
import moe.ofs.backend.object.Unit;
import moe.ofs.backend.connector.util.LuaScripts;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AirdromeDataCollector class is used to collect parking data from each airbases
 * The collector is supposed to be used in single player environment to be able to correctly polling data
 */

public class AirdromeDataCollector {
    private static final List<ParkingInfo> parkingList = new ArrayList<>();

    private static final String sampleAirplaneType = "Christen Eagle II";
    private static final String sampleHelicopterType = "OH-58D";
    private static final String sampleUnitName = "Test Unit";

    public static void collect() {

        parkingList.clear();

        // load script
        String script = LuaScripts.load("add_group.lua");
        String mapTheater =
                LuaScripts.request(LuaQueryEnv.MISSION_SCRIPTING, "return env.mission.theatre").get();

        // build sample unit
        Unit.UnitBuilder airplaneBuilder = new Unit.UnitBuilder();
        Unit airplaneUnit = airplaneBuilder.setCategory(Unit.Category.AIRPLANE).setName(sampleUnitName)
                .setSkill(Unit.Skill.HIGH)
                .setType(sampleAirplaneType).build();

        Unit.UnitBuilder takeOffAirplaneBuilder = new Unit.UnitBuilder();
        Unit takeOffAirplane = takeOffAirplaneBuilder.setCategory(Unit.Category.AIRPLANE).setName(sampleUnitName)
                .setSkill(Unit.Skill.HIGH).setType(sampleAirplaneType).build();

        Unit.UnitBuilder helicopterBuilder = new Unit.UnitBuilder();
        Unit helicopterUnit = helicopterBuilder.setCategory(Unit.Category.HELICOPTER).setName(sampleUnitName)
                .setSkill(Unit.Skill.HIGH)
                .setType(sampleHelicopterType).build();

        Gson gson = new Gson();

        int totalAirbasesNum =
                LuaScripts.request(LuaQueryEnv.MISSION_SCRIPTING, "return #world.getAirbases()").getAsInt();


        for (int airbaseListIndex = 1; airbaseListIndex <= totalAirbasesNum; airbaseListIndex++) {
            String prep = String.format("return #world.getAirbases()[%d]:getParking()", airbaseListIndex);
            int totalParkings = LuaScripts.request(LuaQueryEnv.MISSION_SCRIPTING, prep).getAsInt();
            for (int parkingListIndex = 1; parkingListIndex <= totalParkings; parkingListIndex++) {
                String getParkingLua =
                        LuaScripts.load("get_parking_by_airdrome_id_and_parking_list_index.lua");
                String prepGetParkingLua =
                        String.format(getParkingLua, airbaseListIndex, parkingListIndex);
                String res = LuaScripts.request(LuaQueryEnv.MISSION_SCRIPTING, prepGetParkingLua).get();

                ParkingInfo parking = gson.fromJson(res, ParkingInfo.class);

                int typeConst = parking.getTerminalType();
                int termIndex = parking.getParkingId();

                // avoid using airbaseListIndex because airdromeId is messed up in Caucasus map
                // TODO --> deal with caucasus mess-up
//                int airdromeIdOfParking = parking.getAirdromeId();
                int airdromeIdOfParking;

                // this value is independent on the actual id (runtime id rather than airdromeId) of the airbase
                // it's risky but probably is the only way out of this mess
                parking.setAirdromeId(airbaseListIndex);

                if(mapTheater.equals("Caucasus")) {
                    airdromeIdOfParking = airbaseListIndex + 11;
                } else {
                    airdromeIdOfParking = airbaseListIndex;
                }

                Group.GroupBuilder groupBuilder;
                if(typeConst == 40) {  // Helicopter only spawn
                    // use helicopter
                    groupBuilder = helicopterUnit
                            .toGroupBuilderWithRouteOfInitialParking(
                                    airdromeIdOfParking, termIndex)
                            .setUncontrolled(true);
                } else if(typeConst == 16) {  // runway
                    groupBuilder = takeOffAirplane
                            .toGroupBuilderWithRouteOfIntialRunwayTakeOff(
                                    airdromeIdOfParking, termIndex);
                } else {
                    groupBuilder = airplaneUnit
                            .toGroupBuilderWithRouteOfInitialParking(
                                    airdromeIdOfParking, termIndex)
                            .setUncontrolled(true);
                }
                String preparedScript = String.format(script, gson.toJson(groupBuilder.build()));
                double heading = LuaScripts.request(LuaQueryEnv.MISSION_SCRIPTING, preparedScript).getAsDouble();

                parking.setInitialHeading(heading);
                parkingList.add(parking);

                System.out.printf("Pulling data for %s(%d), idx: %d, type: %d, term: %d -> heading %f%n\n",
                        parking.getAirdromeName(), airbaseListIndex, parkingListIndex, typeConst, termIndex, heading);
            }
        }

        // after filling parkingList
        System.out.println(parkingList.stream()
                .collect(Collectors.groupingBy(ParkingInfo::getAirdromeName, Collectors.counting())));

        // serialize to file
        try {
            Path path = Paths.get( "backend-core/src/main/resources/data").resolve(mapTheater + ".apron");

//            Path path = Paths.get(System.getProperty("user.home")).resolve(mapTheater + ".apron");

            System.out.println(path.toAbsolutePath());
            FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(parkingList);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
