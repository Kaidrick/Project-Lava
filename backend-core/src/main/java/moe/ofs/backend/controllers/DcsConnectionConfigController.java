package moe.ofs.backend.controllers;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.object.PortConfig;
import moe.ofs.backend.util.ConnectionManager;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/config")
public class DcsConnectionConfigController {

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public String getCurrentConfiguration() {
        return "good";
    }

    @RequestMapping(value = "/port", method = RequestMethod.POST)
    public String setConnectionPort(@RequestBody PortConfig config) {
        log.info(config.toString());

        // set request handler connection port number
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        Map<Level, Integer> map = new HashMap<>();
        map.put(Level.SERVER, config.getServerMainPort());
        map.put(Level.SERVER_POLL, config.getServerPollPort());
        map.put(Level.EXPORT, config.getExportMainPort());
        map.put(Level.EXPORT_POLL, config.getExportPollPort());

        connectionManager.setPortOverrideMap(map);
        return "good!";
    }
}
