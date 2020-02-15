package ofs.backend.core.box;

import ofs.backend.core.object.ExportObject;
import ofs.backend.core.request.export.handler.ExportUnitDespawnObservable;
import ofs.backend.core.request.export.handler.ExportUnitSpawnObservable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class BoxOfExportUnit {
    private static volatile List<ExportObject> box = new ArrayList<>();

    public static List<ExportObject> peek() {
        return new ArrayList<>(box);
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
