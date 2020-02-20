package moe.ofs.backend.controllers;

import moe.ofs.backend.services.ExportObjectService;
import org.springframework.stereotype.Controller;

@Controller
public class ExportObjectController {
    private final ExportObjectService exportObjectService;

    public ExportObjectController(ExportObjectService exportObjectService) {
        this.exportObjectService = exportObjectService;
    }

    public String getDesc() {
        return exportObjectService.getInfo();
    }
}
