package moe.ofs.backend.controllers;

import moe.ofs.backend.services.BoxOfExportUnitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BoxOfExportObjectController {

    private final BoxOfExportUnitService boxOfExportUnitService;

    public BoxOfExportObjectController(BoxOfExportUnitService boxOfExportUnitService) {
        this.boxOfExportUnitService = boxOfExportUnitService;
    }

    @RequestMapping("/export")
    public String getExportObjects(Model model) {

        model.addAttribute("exportObjects", boxOfExportUnitService.getAll());

        return "index";
    }
}
