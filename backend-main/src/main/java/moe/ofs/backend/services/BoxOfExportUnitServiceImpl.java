package moe.ofs.backend.services;

import moe.ofs.backend.dataset.ExportUnitDataSet;
import moe.ofs.backend.object.ExportObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BoxOfExportUnitServiceImpl implements BoxOfExportUnitService {

    private final ExportObjectHashMapUpdateService exportObjectHashMapUpdateService;

    public BoxOfExportUnitServiceImpl(ExportObjectHashMapUpdateService exportObjectHashMapUpdateService) {
        this.exportObjectHashMapUpdateService = exportObjectHashMapUpdateService;
    }

    @Override
    public List<ExportObject> getAll() {
        return new ArrayList<>(exportObjectHashMapUpdateService.findAll().values());
    }
}
