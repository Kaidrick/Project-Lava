package moe.ofs.backend.handlers;

import moe.ofs.backend.domain.PlayerInfo;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface PlayerLeaveServerObservable {
    List<PlayerLeaveServerObservable> list = new ArrayList<>();

    void observe(PlayerInfo playerInfo);

    default void register() {
        list.add(this);
    }
    default void unregister() {
        list.remove(this);
    }

    static void invokeAll(PlayerInfo playerInfo) {
        new ArrayList<>(list).forEach(o -> o.observe(playerInfo));
    }
}
