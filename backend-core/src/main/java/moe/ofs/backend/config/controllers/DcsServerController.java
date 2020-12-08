package moe.ofs.backend.config.controllers;

import moe.ofs.backend.config.model.ServerResetAction;
import moe.ofs.backend.config.services.DcsSimulationControlService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("server")
public class DcsServerController {

    private final DcsSimulationControlService service;

    public DcsServerController(DcsSimulationControlService service) {
        this.service = service;
    }

    // restart
    @RequestMapping(value = "/control/reload_mission", method = RequestMethod.POST)
    public boolean reloadCurrentMission(@RequestBody ServerResetAction serverResetAction) {
        return service.restart(serverResetAction.getResetType());
    }

    // shutdown

    // background task service halt

    // lava system shutdown: shutdown background tasks and task dispatcher, then shutdown spring boot starter actuator

}
