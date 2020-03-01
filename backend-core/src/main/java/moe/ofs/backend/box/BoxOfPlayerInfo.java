package moe.ofs.backend.box;

import moe.ofs.backend.object.PlayerInfo;
import moe.ofs.backend.handlers.PlayerEnterServerObservable;
import moe.ofs.backend.handlers.PlayerLeaveServerObservable;
import moe.ofs.backend.handlers.PlayerSlotChangeObservable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class BoxOfPlayerInfo {
    public static volatile Map<String, PlayerInfo> box = new HashMap<>();

    public static Map<String, PlayerInfo> peek() {
        return new HashMap<>(box);
    }

    public static void observeAll(Map<String, PlayerInfo> map) {

        for (int i = 5; i < 20; i++) {
            PlayerInfo playerInfo = new PlayerInfo();
            playerInfo.setId(i);
            playerInfo.setName("test" + i);
            playerInfo.setIpaddr("dfasdfsd");
            playerInfo.setLang("cn");
            playerInfo.setPing(999);
            playerInfo.setSide(1);
            playerInfo.setSlot("1247");
            playerInfo.setUcid("2579384yhtfgn39845ygh94");
            playerInfo.setStarted(true);
            map.put("test" + i, playerInfo);
        }


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

    public static PlayerInfo findByName(String name) {
        return box.get(name);
    }
}
