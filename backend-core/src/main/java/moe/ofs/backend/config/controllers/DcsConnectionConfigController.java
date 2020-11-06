package moe.ofs.backend.config.controllers;

import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicResponseParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.object.PortConfig;
import moe.ofs.backend.util.ConnectionManager;
import moe.ofs.backend.util.DcsScriptConfigManager;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/config")
@Api("配置")
public class DcsConnectionConfigController {

    private final ConnectionManager connectionManager;

    private final DcsScriptConfigManager configManager = new DcsScriptConfigManager();

    public DcsConnectionConfigController(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ApiOperation("获取当前配置")
    @DynamicResponseParameters(properties = {
            @DynamicParameter(name = "portMapping", value = "Map<Level, Integer>", dataTypeClass = HashMap.class)
    })
    public PortConfig getCurrentConfiguration() {
        Map<Level, Integer> portMapping = connectionManager.getPortOverrideMap();
        return PortConfig.builder()
                .serverMainPort(portMapping.get(Level.SERVER))
                .serverPollPort(portMapping.get(Level.SERVER_POLL))
                .exportMainPort(portMapping.get(Level.EXPORT))
                .exportPollPort(portMapping.get(Level.EXPORT_POLL))
                .build();
    }

    @RequestMapping(value = "/port", method = RequestMethod.POST)
    @ApiOperation("设置配置")
    @DynamicResponseParameters(properties = {
            @DynamicParameter(value = "PortConfig", dataTypeClass = PortConfig.class)
    })
    public PortConfig setConnectionPort(
            @ApiParam(value = "PortConfig")
            @RequestBody PortConfig config
    ) {
        log.info(config.toString());

        // set request handler connection port number
        Map<Level, Integer> map = new HashMap<>();

        map.put(Level.SERVER, config.getServerMainPort());
        map.put(Level.SERVER_POLL, config.getServerPollPort());
        map.put(Level.EXPORT, config.getExportMainPort());
        map.put(Level.EXPORT_POLL, config.getExportPollPort());

        connectionManager.setPortOverrideMap(map);

        Map<Level, Integer> portMapping = connectionManager.getPortOverrideMap();
        return PortConfig.builder()
                .serverMainPort(portMapping.get(Level.SERVER))
                .serverPollPort(portMapping.get(Level.SERVER_POLL))
                .exportMainPort(portMapping.get(Level.EXPORT))
                .exportPollPort(portMapping.get(Level.EXPORT_POLL))
                .build();
    }

    @RequestMapping(value = "/port_reset", method = RequestMethod.GET)
    public PortConfig resetDefaultConnectionPort() {
        connectionManager.restoreDefaultPortMap();
        return getCurrentConfiguration();
    }

    @RequestMapping(value = "/script/install/{branch}", method = RequestMethod.GET)
    public void installScripts(@PathVariable String branch) {
        configManager.injectIntoHooks(Paths.get(branch));
        configManager.injectIntoExport(Paths.get(branch));
    }

    @RequestMapping(value = "/script/uninstall/{branch}", method = RequestMethod.GET)
    public void uninstallScripts(@PathVariable String branch) {
        configManager.removeInjection(Paths.get(branch));
    }

    @RequestMapping(value = "/script/branch", method = RequestMethod.GET)
    public List<String> getBranches() throws IOException {
        return configManager.getUserDcsWritePaths().stream().map(path -> path.getName(path.getNameCount() - 1).toString()).collect(Collectors.toList());
    }

    @RequestMapping(value = "/script/branch/{name}", method = RequestMethod.POST)
    public boolean isInjected(@PathVariable String name) {
        return configManager.isInjectionConfigured(Paths.get(name));
    }
}
