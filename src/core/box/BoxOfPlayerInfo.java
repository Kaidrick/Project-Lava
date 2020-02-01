package core.box;

import core.object.PlayerInfo;
import core.request.server.handler.PlayerEnterServerObservable;
import core.request.server.handler.PlayerLeaveServerObservable;
import core.request.server.handler.PlayerSlotChangeObservable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class BoxOfPlayerInfo {
    public static volatile Map<String, PlayerInfo> box = new HashMap<>();

    public static void observeAll(Map<String, PlayerInfo> map) {

        map.keySet().stream()
                .filter(k -> !box.containsKey(k))  // map key not in box
                .forEach(r -> PlayerEnterServerObservable.invokeAll(map.get(r)));

        // box key not in new map, player disconnected
        box.keySet().stream()
                .filter(k -> !map.containsKey(k))  // box key not in map
                .forEach(r -> PlayerLeaveServerObservable.invokeAll(box.get(r)));

        // put new map key value into box, remove obsolete key value
        // check for slot change, if slot change, call lots of registered methods
        Stream<String> keyStream = map.keySet().stream().filter(k -> box.containsKey(k));
        keyStream.forEach(k -> box.computeIfPresent(k, (boxKey, playerInfo) -> {
            if(!playerInfo.equals(map.get(k))) {
                PlayerSlotChangeObservable.invokeAll(playerInfo, map.get(k));
            }
            return null;
        }));

        box.clear();
        box.putAll(map);
    }
}
