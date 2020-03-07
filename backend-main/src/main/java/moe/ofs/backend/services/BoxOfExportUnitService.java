package moe.ofs.backend.services;

import moe.ofs.backend.domain.ExportObject;

import java.util.List;

public interface BoxOfExportUnitService {
    List<ExportObject> getAll();
}
