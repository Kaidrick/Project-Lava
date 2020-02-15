package ofs.backend.core.request.server.handler;

import ofs.backend.core.object.PlayerInfo;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface PlayerSlotChangeObservable {
    // list of class?
    List<PlayerSlotChangeObservable> list = new ArrayList<>();

    void observe(PlayerInfo previous, PlayerInfo current);

    default void register() {
        list.add(this);
    }
    default void unregister() {
        list.remove(this);
    }

    static void invokeAll(PlayerInfo previous, PlayerInfo current) {
        list.forEach(o -> o.observe(previous, current));
    }
}
