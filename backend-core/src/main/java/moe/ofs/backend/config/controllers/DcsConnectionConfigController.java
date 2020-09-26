package moe.ofs.backend.config.controllers;

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
public class DcsConnectionConfigController {

    private final DcsScriptConfigManager manager = new DcsScriptConfigManager();

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public PortConfig getCurrentConfiguration() {
        Map<Level, Integer> portMapping = ConnectionManager.getInstance().getPortOverrideMap();
        return PortConfig.builder()
                .serverMainPort(portMapping.get(Level.SERVER))
                .serverPollPort(portMapping.get(Level.SERVER_POLL))
                .exportMainPort(portMapping.get(Level.EXPORT))
                .exportPollPort(portMapping.get(Level.EXPORT_POLL))
                .build();
    }

    @RequestMapping(value = "/port", method = RequestMethod.POST)
    public PortConfig setConnectionPort(@RequestBody PortConfig config) {
        log.info(config.toString());

        // set request handler connection port number
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        Map<Level, Integer> map = new HashMap<>();

        map.put(Level.SERVER, config.getServerMainPort());
        map.put(Level.SERVER_POLL, config.getServerPollPort());
        map.put(Level.EXPORT, config.getExportMainPort());
        map.put(Level.EXPORT_POLL, config.getExportPollPort());

        connectionManager.setPortOverrideMap(map);

        Map<Level, Integer> portMapping = ConnectionManager.getInstance().getPortOverrideMap();
        return PortConfig.builder()
                .serverMainPort(portMapping.get(Level.SERVER))
                .serverPollPort(portMapping.get(Level.SERVER_POLL))
                .exportMainPort(portMapping.get(Level.EXPORT))
                .exportPollPort(portMapping.get(Level.EXPORT_POLL))
                .build();
    }

    @RequestMapping(value = "/script/install/{branch}", method = RequestMethod.GET)
    public void installScripts(@PathVariable String branch) {
        manager.injectIntoHooks(Paths.get(branch));
        manager.injectIntoExport(Paths.get(branch));
    }

    @RequestMapping(value = "/script/uninstall/{branch}", method = RequestMethod.GET)
    public void uninstallScripts(@PathVariable String branch) {
        manager.removeInjection(Paths.get(branch));
    }

    @RequestMapping(value = "/script/branch", method = RequestMethod.GET)
    public List<String> getBranches() throws IOException {
        return manager.getUserDcsWritePaths().stream().map(path -> path.getName(path.getNameCount() - 1).toString()).collect(Collectors.toList());
    }

    @RequestMapping(value = "/script/branch/{name}", method = RequestMethod.POST)
    public boolean isInjected(@PathVariable String name) {
        return manager.isInjectionConfigured(Paths.get(name));
    }
}
