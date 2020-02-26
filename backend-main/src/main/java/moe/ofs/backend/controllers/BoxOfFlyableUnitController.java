package moe.ofs.backend.controllers;

import moe.ofs.backend.services.BoxOfFlyableUnitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BoxOfFlyableUnitController {

    private final BoxOfFlyableUnitService boxOfFlyableUnitService;

    public BoxOfFlyableUnitController(BoxOfFlyableUnitService boxOfFlyableUnitService) {
        this.boxOfFlyableUnitService = boxOfFlyableUnitService;
    }

    @RequestMapping("/flyable")
    public String getFlyableUnits(Model model) {

        model.addAttribute("flyableUnits", boxOfFlyableUnitService.getAll());

        return "flyable/index";
    }
}
