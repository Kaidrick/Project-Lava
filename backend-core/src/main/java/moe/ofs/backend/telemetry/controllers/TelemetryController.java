package moe.ofs.backend.telemetry.controllers;

import moe.ofs.backend.dispatcher.model.LavaTask;
import moe.ofs.backend.object.TelemetryData;
import moe.ofs.backend.telemetry.serivces.LuaStateTelemetryService;
import moe.ofs.backend.telemetry.serivces.MetaTelemetryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("telemetry")
public class TelemetryController {

    private final LuaStateTelemetryService luaStateTelemetryService;
    private final MetaTelemetryService metaTelemetryService;

    public TelemetryController(LuaStateTelemetryService luaStateTelemetryService, MetaTelemetryService metaTelemetryService) {
        this.luaStateTelemetryService = luaStateTelemetryService;
        this.metaTelemetryService = metaTelemetryService;
    }

    @RequestMapping(value = "all", method = RequestMethod.GET)
    public Set<TelemetryData> getTelemetryData() {
        return luaStateTelemetryService.findAll();
    }

    @RequestMapping(value = "taskCount", method = RequestMethod.GET)
    public int getDispatcherTaskCount() {
        return metaTelemetryService.getDispatcherTaskCount();
    }

    @RequestMapping(value = "dispatcherTasks", method = RequestMethod.GET)
    public List<LavaTask> getDispatcherTasks() {
        return metaTelemetryService.findAllDispatcherTasks();
    }
}
