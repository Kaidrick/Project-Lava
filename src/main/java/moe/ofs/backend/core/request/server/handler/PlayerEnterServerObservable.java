package moe.ofs.backend.core.request.server.handler;

import moe.ofs.backend.core.object.PlayerInfo;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface PlayerEnterServerObservable {
    List<PlayerEnterServerObservable> list = new ArrayList<>();

    void observe(PlayerInfo playerInfo);

    default void register() {
        list.add(this);
    }
    default void unregister() {
        list.remove(this);
    }

    static void invokeAll(PlayerInfo playerInfo) {
        list.forEach(o -> o.observe(playerInfo));
    }
}
