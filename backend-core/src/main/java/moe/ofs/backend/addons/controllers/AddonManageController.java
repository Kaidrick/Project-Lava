package moe.ofs.backend.addons.controllers;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.addons.model.PluginVo;
import moe.ofs.backend.addons.services.AddonRegistryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
public class AddonManageController {

    private final AddonRegistryService service;

    public AddonManageController(AddonRegistryService service) {
        this.service = service;
    }

    @GetMapping("/view/{ident}")
    public String test(@PathVariable String ident) {
        log.info("ident = {}", ident);
        return "forward:/" + ident + "/index.html";
    }

    @GetMapping("/addon/list")
    @ResponseBody
    public List<PluginVo> getPluginList() {
        return service.findAll();
    }
}
