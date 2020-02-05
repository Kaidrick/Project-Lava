package core;

import java.util.ArrayList;
import java.util.List;

public interface MissionStartObservable {
    List<MissionStartObservable> list = new ArrayList<>();

    void observe();

    default void register() {
        list.add(this);
    }
    default void unregister() {
        list.remove(this);
    }

    static void invokeAll() {
        list.forEach(MissionStartObservable::observe);
    }
}
