package core.request.export.handler;

import core.object.ExportObject;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface ExportUnitDespawnObservable {
    List<ExportUnitDespawnObservable> list = new ArrayList<>();

    void observe(ExportObject exportObject);

    default void register() {
        list.add(this);
    }
    default void unregister() {
        list.remove(this);
    }

    static void invokeAll(ExportObject exportObject) {
        list.forEach(o -> o.observe(exportObject));
    }
}
