package moe.ofs.backend.handlers;

import moe.ofs.backend.domain.PlayerInfo;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface PlayerSlotChangeObservable {

    List<PlayerSlotChangeObservable> list = new ArrayList<>();

    void observe(PlayerInfo previous, PlayerInfo current);

    default void register() {
        list.add(this);
    }
    default void unregister() {
        list.remove(this);
    }

    static void invokeAll(PlayerInfo previous, PlayerInfo current) {
        new ArrayList<>(list).forEach(o -> o.observe(previous, current));
    }
}
