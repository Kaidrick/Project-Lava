package moe.ofs.backend.request.server;

import moe.ofs.backend.domain.Handle;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.LavaLog;
import moe.ofs.backend.request.DataRequest;
import moe.ofs.backend.request.Resolvable;
import moe.ofs.backend.util.lua.LuaQueryEnv;

/**
 * The constructor of ServerExecRequest can take a RequestToMission
 */

public class ServerExecRequest extends DataRequest implements Resolvable {
    {
        handle = Handle.EXEC;
        port = 3010;
        state = LuaQueryEnv.MISSION_SCRIPTING;
    }

    private final LavaLog.Logger logger = LavaLog.getLogger(ServerExecRequest.class);

    private transient String env;
    private transient String luaString;

    public ServerExecRequest(String luaString) {
        super(Level.SERVER);
        this.luaString = luaString;
        this.env = this.state.getEnv().toLowerCase();
    }

    // FIXME --> state doesn't work
    public ServerExecRequest(LuaQueryEnv state, String luaString) {
        super(Level.SERVER);
        this.state = state;
        this.luaString = luaString;
        this.env = this.state.getEnv().toLowerCase();
    }

    @Override
    public void resolve(String object) {
        String logMessage = luaString + "\nReturns: " + object;
        logger.debug(logMessage);
    }
}

