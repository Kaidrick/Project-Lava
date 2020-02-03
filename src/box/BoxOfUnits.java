package core.box;

import core.object.Group;
import core.object.Unit;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BoxOfUnits {
    private static ConcurrentMap<String, Unit> box = new ConcurrentHashMap<>();

//    public static void updateBox(String unitName, Unit unitObject) {
//        if(box.containsKey(unitName)) {
//            System.out.println("existing unit update!");
//        } else {
//            System.out.println("new unit spawn!");
//        }
//        box.put(unitName, unitObject);
//
//        box.entrySet().stream()
//                .filter(stringUnitEntry -> );
//    }

    public static void putBox(ConcurrentMap<String, Unit> newMap) {

        // compute if absent -> newMap to box -> new unit?
        // compute if absent -> box to newMap -> outdated unit?

        List<Unit> newSpawnList = newMap.entrySet().stream()
                .filter(entry -> !box.containsKey(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        List<Unit> oldDespawnList = box.entrySet().stream()
                .filter(entry -> !newMap.containsKey(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        box = new ConcurrentHashMap<>(newMap);

        newSpawnList.forEach(unit -> System.out.println("SPAWN - " + unit.getName()));
        oldDespawnList.forEach(unit -> System.out.println("DESPAWN - " + unit.getName()));

    }


    // update box
    // if new unit, generate events
    // if unit no long exist, generate events
}
