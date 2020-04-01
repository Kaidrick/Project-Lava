package moe.ofs.backend.handlers;

import java.util.ArrayList;
import java.util.List;

public interface MissionStartObservable {
    List<MissionStartObservable> list = new ArrayList<>();

    void observe(String theaterName);

    default void register() {
        list.add(this);
    }
    default void unregister() {
        list.remove(this);
    }

    static void invokeAll(String theaterName) {
        list.forEach(o -> new Thread(() -> o.observe(theaterName)).start());
    }
}
