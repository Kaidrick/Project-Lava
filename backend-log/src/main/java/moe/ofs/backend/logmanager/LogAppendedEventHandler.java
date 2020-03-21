package moe.ofs.backend.logmanager;

import java.util.ArrayList;
import java.util.List;

public interface LogAppendedEventHandler {

    List<LogAppendedEventHandler> list = new ArrayList<>();

    void update(LogEntry logEntry);

    default void attach() {
        list.add(this);
    }

    default void dettach() {
        list.remove(this);
    }

    static void invokeAll(LogEntry logEntry) {
        list.forEach(h -> h.update(logEntry));
    }

}
