package moe.ofs.backend.request.server;

import moe.ofs.backend.domain.Handle;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.lavalog.LavaLog;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.Resolvable;

/**
 * The constructor of ServerExecRequest can take a RequestToMission
 */

public class ServerExecRequest extends RequestToServer implements Resolvable {
    {
        handle = Handle.EXEC;
        port = 3010;
        state = State.SERVER;
    }

    private final LavaLog.Logger logger = LavaLog.getLogger(ServerExecRequest.class);

    private transient String env;
    private transient String luaString;

    public ServerExecRequest(String luaString) {
        super(Level.SERVER);
        this.luaString = luaString;
        this.env = this.state.name().toLowerCase();
    }

    // FIXME --> state doesn't work
    public ServerExecRequest(State state, String luaString) {
        super(Level.SERVER);
        this.state = state;
        this.luaString = luaString;
        this.env = this.state.name().toLowerCase();
    }

    @Override
    public void resolve(String object) {
        String logMessage = luaString + "\nReturns: " + object;
        logger.debug(logMessage);
    }
}

