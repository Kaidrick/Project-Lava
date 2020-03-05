package moe.ofs.backend.dataset;

import com.google.common.collect.Sets;
import moe.ofs.backend.handlers.ExportUnitDespawnObservable;
import moe.ofs.backend.handlers.ExportUnitSpawnObservable;
import moe.ofs.backend.object.ExportObject;
import moe.ofs.backend.services.ExportObjectHashMapUpdateService;
import moe.ofs.backend.services.ExportObjectJpaService;
import moe.ofs.backend.util.Logger;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ExportUnitDataSet {

    private final ExportObjectHashMapUpdateService exportObjectHashMapUpdateService;
    private final ExportObjectJpaService exportObjectJpaService;

    public ExportUnitDataSet(ExportObjectHashMapUpdateService exportObjectHashMapUpdateService,
                             ExportObjectJpaService exportObjectJpaService) {
        this.exportObjectHashMapUpdateService = exportObjectHashMapUpdateService;
        this.exportObjectJpaService = exportObjectJpaService;
    }

    public void init() {

        exportObjectHashMapUpdateService.dispose();
        Logger.log("ExportObjectHashMapRepository disposed");

        exportObjectJpaService.dispose();
        Logger.log("ExportObjectH2Data disposed");

    }

    private void processUpdateExportObject(ExportObject exportObject) {
        exportObjectHashMapUpdateService.update(exportObject);
        exportObjectJpaService.save(exportObject);
    }

    private void processNewExportObject(ExportObject exportObject) {
        ExportUnitSpawnObservable.invokeAll(exportObject);
        exportObjectHashMapUpdateService.save(exportObject);
        exportObjectJpaService.save(exportObject);
    }

    private void processObsoleteExportObject(ExportObject exportObject) {
        ExportUnitDespawnObservable.invokeAll(exportObject);
        exportObjectHashMapUpdateService.delete(exportObject);
        exportObjectJpaService.delete(exportObject);
    }

    public void cycle(List<ExportObject> list) {

        Set<ExportObject> record = new HashSet<>(exportObjectHashMapUpdateService.findAll().values());
        Set<ExportObject> update = new HashSet<>(list);



        // ExportObjects whose data needs to be updated
        Sets.SetView<ExportObject> intersection = Sets.intersection(record, update);

        // new spawn
        // ExportObjects whose data is obsolete to data set and need to be removed
        Sets.SetView<ExportObject> despawn = Sets.symmetricDifference(intersection, record);

        // obsolete despawn
        // ExportObjects whose data is new to data set and need to be added
        Sets.SetView<ExportObject> spawn = Sets.symmetricDifference(intersection, update);


        intersection.forEach(this::processUpdateExportObject);
        despawn.forEach(this::processObsoleteExportObject);
        spawn.forEach(this::processNewExportObject);

    }
}
