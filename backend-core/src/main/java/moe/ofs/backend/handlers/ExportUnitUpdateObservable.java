package moe.ofs.backend.handlers;

import moe.ofs.backend.domain.dcs.poll.ExportObject;

import java.util.ArrayList;
import java.util.List;

public interface ExportUnitUpdateObservable {
    List<ExportUnitUpdateObservable> list = new ArrayList<>();

    void observe(ExportObject previous, ExportObject current);

    default void register() {
        list.add(this);
    }
    default void unregister() {
        list.remove(this);
    }

    static void invokeAll(ExportObject previous, ExportObject current) {
        new ArrayList<>(list).forEach(o -> o.observe(previous, current));
    }
}
