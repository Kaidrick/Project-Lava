package moe.ofs.backend.services;

import moe.ofs.backend.box.BoxOfExportUnit;
import moe.ofs.backend.object.ExportObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoxOfExportObjectServiceImpl implements BoxOfExportUnitService {
    @Override
    public List<ExportObject> getAll() {
        return BoxOfExportUnit.peek();
    }
}
