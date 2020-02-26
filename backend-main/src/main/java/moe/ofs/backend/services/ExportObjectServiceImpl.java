package moe.ofs.backend.services;

import org.springframework.stereotype.Service;

@Service
public class ExportObjectServiceImpl implements ExportObjectService {

    private final BoxOfExportUnitService boxOfExportUnitService;

    public ExportObjectServiceImpl(BoxOfExportUnitService boxOfExportUnitService) {
        this.boxOfExportUnitService = boxOfExportUnitService;
    }

    @Override
    public String getInfo() {
        return "info!";
    }

    @Override
    public String getPosition() {
        return "position!";
    }

    // group id is in playable box
    @Override
    public Integer getGroupId() {


        return null;
    }
}
