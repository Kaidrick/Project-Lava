package moe.ofs.backend.config.controllers;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.connector.Level;
import moe.ofs.backend.object.PortConfig;
import moe.ofs.backend.connector.ConnectionManager;
import moe.ofs.backend.connector.DcsScriptConfigManager;
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
@Api(tags = "配置管理")
@ApiSupport(author = "北欧式的简单")
public class DcsConnectionConfigController {

    private final ConnectionManager connectionManager;

    private final DcsScriptConfigManager configManager = new DcsScriptConfigManager();

    public DcsConnectionConfigController(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取配置")
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
    @ApiOperation(value = "设置端口")
    public PortConfig setConnectionPort(
            @ApiParam
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

    @RequestMapping(value = "/port_reset", method = RequestMethod.GET)  // TODO: fix underscore
    @ApiOperation(value = "重置端口")
    public PortConfig resetDefaultConnectionPort() {
        connectionManager.restoreDefaultPortMap();
        return getCurrentConfiguration();
    }

    @RequestMapping(value = "/script/install/{branch}", method = RequestMethod.GET)
    @ApiOperation(value = "脚本安装")
    public void installScripts(
            @ApiParam(value = "DCS版本", example = "DCS.openbeta_server")
            @PathVariable String branch
    ) {
        configManager.injectIntoHooks(Paths.get(branch));
        configManager.injectIntoExport(Paths.get(branch));
    }

    @RequestMapping(value = "/script/uninstall/{branch}", method = RequestMethod.GET)
    @ApiOperation(value = "脚本卸载")
    public void uninstallScripts(
            @ApiParam(value = "DCS版本", example = "DCS.openbeta_server")
            @PathVariable String branch
    ) {
        configManager.removeInjection(Paths.get(branch));
    }

    @RequestMapping(value = "/script/branch", method = RequestMethod.GET)
    @ApiOperation(value = "获取游戏版本")
    public List<String> getBranches() throws IOException {
        return configManager.getUserDcsWritePaths().stream().map(path -> path.getName(path.getNameCount() - 1).toString()).collect(Collectors.toList());
    }

    @RequestMapping(value = "/script/branch/{name}", method = RequestMethod.POST)
    @ApiOperation(value = "判断该DCS是否已安装脚本")
    public boolean isInjected(
            @ApiParam(value = "DCS版本", example = "DCS.openbeta_server")
            @PathVariable String name
    ) {
        return configManager.isInjectionConfigured(Paths.get(name));
    }
}
