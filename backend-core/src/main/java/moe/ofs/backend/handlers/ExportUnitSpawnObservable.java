package moe.ofs.backend.handlers;

import moe.ofs.backend.domain.dcs.poll.ExportObject;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface ExportUnitSpawnObservable {
    List<ExportUnitSpawnObservable> list = new ArrayList<>();

    void observe(ExportObject exportObject);

    default void register() {
        list.add(this);
    }
    default void unregister() {
        list.remove(this);
    }

    static void invokeAll(ExportObject exportObject) {
        new ArrayList<>(list).forEach(o -> o.observe(exportObject));
    }
}
