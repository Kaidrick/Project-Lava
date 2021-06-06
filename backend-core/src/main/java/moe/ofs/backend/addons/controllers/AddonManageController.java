package moe.ofs.backend.addons.controllers;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicResponseParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.addons.model.PluginVo;
import moe.ofs.backend.addons.services.AddonRegistryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@Slf4j
@Api(tags = "插件管理API")
@ApiSupport(author = "北欧式的简单")
public class AddonManageController {

    private final AddonRegistryService service;

    public AddonManageController(AddonRegistryService service) {
        this.service = service;
    }

    @GetMapping("/view/{ident}")
    @ApiOperation(value = "")
    public String test(
            @PathVariable String ident
    ) {
        log.info("ident = {}", ident);
        return "forward:/" + ident + "/index.html";
    }

    @GetMapping("/addon/list")
    @ResponseBody
    @ApiOperation(value = "获取插件列表")
    public List<PluginVo> getPluginList() {
        return service.findAll();
    }
}
