package moe.ofs.backend.config.services.impl;

import moe.ofs.backend.config.model.ResetType;
import moe.ofs.backend.config.services.DcsSimulationControlService;
import moe.ofs.backend.connector.util.LuaScripts;
import moe.ofs.backend.connector.lua.LuaQueryCapable;
import moe.ofs.backend.connector.lua.LuaQueryState;
import moe.ofs.backend.connector.lua.LuaQueryEnv;
import org.springframework.stereotype.Service;

@Service
@LuaQueryState(LuaQueryEnv.SERVER_CONTROL)
public class DcsSimulationControlServiceImpl
        implements DcsSimulationControlService, LuaQueryCapable {

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

        return query(luaString).getAsBoolean();
    }

    @Override
    public void loadMission(String missionName) {

    }

    @Override
    public void shutdown(ResetType type) {

    }
}
