package moe.ofs.backend.config.controllers;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.config.model.ServerResetAction;
import moe.ofs.backend.config.services.DcsNetworkControlService;
import moe.ofs.backend.config.services.DcsSimulationControlService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("server")
@Slf4j
@Api(tags = "服务器管理")
@ApiSupport(author = "北欧式的简单")
public class DcsServerController {

    private final DcsSimulationControlService service;
    private final DcsNetworkControlService networkControlService;

    public DcsServerController(DcsSimulationControlService service,
                               DcsNetworkControlService networkControlService) {
        this.service = service;
        this.networkControlService = networkControlService;
    }

    // restart
    @RequestMapping(value = "/control/reload_mission", method = RequestMethod.POST)  // FIXME: fix underscore
    @ApiOperation(value = "重启服务器任务")
    public boolean reloadCurrentMission(
            @ApiParam
            @RequestBody ServerResetAction serverResetAction
    ) {
        return service.restart(serverResetAction.getResetType());
    }

    // shutdown

    // background task service halt

    // lava system shutdown: shutdown background tasks and task dispatcher, then shutdown spring boot starter actuator

    @RequestMapping(value = "/control/block_all", method = RequestMethod.POST)  // FIXME: fix underscore

    public boolean blockServerEntryForAll(
            @ApiParam
            @RequestBody boolean isBlocked
    ) {
        networkControlService.blockServerConnections(isBlocked);
        return true;
    }
}
