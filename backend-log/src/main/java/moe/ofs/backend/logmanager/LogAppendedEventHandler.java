package moe.ofs.backend.logmanager;

import java.util.ArrayList;
import java.util.List;

public interface LogAppendedEventHandler {

    List<LogAppendedEventHandler> list = new ArrayList<>();

    void update(String logText);

    default void attach() {
        list.add(this);
    }

    default void dettach() {
        list.remove(this);
    }

    static void invokeAll(String logText) {
        list.forEach(h -> h.update(logText));
    }

}
