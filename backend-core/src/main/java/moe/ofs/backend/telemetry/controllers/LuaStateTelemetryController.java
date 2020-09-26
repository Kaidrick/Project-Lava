package moe.ofs.backend.telemetry.controllers;

import moe.ofs.backend.object.TelemetryData;
import moe.ofs.backend.telemetry.serivces.LuaStateTelemetryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("telemetry")
public class LuaStateTelemetryController {

    private final LuaStateTelemetryService luaStateTelemetryService;

    public LuaStateTelemetryController(LuaStateTelemetryService luaStateTelemetryService) {
        this.luaStateTelemetryService = luaStateTelemetryService;
    }

    @RequestMapping(value = "all", method = RequestMethod.GET)
    public Set<TelemetryData> getTelemetryData() {
        return luaStateTelemetryService.findAll();
    }
}
