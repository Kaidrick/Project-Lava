package moe.ofs.backend.addons.controllers;

import moe.ofs.backend.addons.model.PluginVo;
import moe.ofs.backend.addons.services.AddonRegistryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AddonManageController {

    private final AddonRegistryService service;

    public AddonManageController(AddonRegistryService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "/view/{ident}")
    public String test(@PathVariable String ident) {
        System.out.println("ident = " + ident);
        return "forward:/" + ident + "/index.html";
    }

    @GetMapping("/addon/list")
    @ResponseBody
    public List<PluginVo> getPluginList() {
        return service.findAll();
    }
}
