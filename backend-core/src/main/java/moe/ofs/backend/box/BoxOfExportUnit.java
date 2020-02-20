package moe.ofs.backend.box;

import moe.ofs.backend.handlers.BackgroundTaskRestartObservable;
import moe.ofs.backend.object.ExportObject;
import moe.ofs.backend.handlers.ExportUnitDespawnObservable;
import moe.ofs.backend.handlers.ExportUnitSpawnObservable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class BoxOfExportUnit {
    private static volatile List<ExportObject> box = new ArrayList<>();

    static {
        BackgroundTaskRestartObservable backgroundTaskRestartObservable = BoxOfExportUnit::dispose;
        backgroundTaskRestartObservable.register();
    }

    public static List<ExportObject> peek() {
        return new ArrayList<>(box);
    }

    public static void dispose() {
        box.clear();
    }

    public static void observeAll(List<ExportObject> list) {

//        System.out.println("list = " + list.size());

        List<String> boxNameList = box.parallelStream().map(ExportObject::getUnitName).collect(Collectors.toList());
        List<String> updateNameList = list.parallelStream().map(ExportObject::getUnitName).collect(Collectors.toList());

        // unit in list but not in box -> new spawn
        list.parallelStream().filter(e -> !boxNameList.contains(e.getUnitName()))
                .forEach(ExportUnitSpawnObservable::invokeAll);

        // unit in box but not in list -> unit despawn
        box.parallelStream().filter(e -> !updateNameList.contains(e.getUnitName()))
                .forEach(ExportUnitDespawnObservable::invokeAll);

        // use partition by and then for each?



        box.clear();
        box.addAll(list);
    }
}
