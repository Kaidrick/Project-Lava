package moe.ofs.backend.config.services.impl;

import moe.ofs.backend.config.model.ResetType;
import moe.ofs.backend.config.services.DcsSimulationControlService;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Service;

@Service
public class DcsSimulationControlServiceImpl implements DcsSimulationControlService {

    // TODO: replace with lua script do service interface
    private final RequestTransmissionService requestTransmissionService;

    public DcsSimulationControlServiceImpl(RequestTransmissionService requestTransmissionService) {
        this.requestTransmissionService = requestTransmissionService;
    }

    @Override
    public boolean restart(ResetType type) {
        // TODO: what about type?
        switch (type) {
            case DCS_SERVER:
                break;
            case BACKGROUND_TASK:
                break;
            case COMPLETE_RESTART:
                break;
            default:
                break;
        }

        String luaString = LuaScripts.load("api/reload_current_mission.lua");
        return ((ServerDataRequest) requestTransmissionService.send(
                new ServerDataRequest(RequestToServer.State.DEBUG, luaString))).getAsBoolean();
    }

    @Override
    public void loadMission(String missionName) {

    }

    @Override
    public void shutdown(ResetType type) {

    }
}
