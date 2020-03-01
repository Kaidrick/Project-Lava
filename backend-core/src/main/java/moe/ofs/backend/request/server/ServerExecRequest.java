package moe.ofs.backend.request.server;

import moe.ofs.backend.request.Resolvable;
import moe.ofs.backend.util.Logger;
import moe.ofs.backend.request.RequestToServer;

/**
 * The constructor of ServerExecRequest can take a RequestToMission
 */

public class ServerExecRequest extends RequestToServer implements Resolvable {
    {
        handle = Handle.EXEC;
        port = 3010;
        state = State.SERVER;
    }

    private transient String env;
    private transient String luaString;

    public ServerExecRequest(String luaString) {
        this.luaString = luaString;
        this.env = this.state.name().toLowerCase();
    }

    public ServerExecRequest(State state, String luaString) {
        this.state = state;
        this.luaString = luaString;
        this.env = this.state.name().toLowerCase();
    }

    @Override
    public void resolve(String object) {
        String logMessage = luaString + "\nReturns: " + object;
        Logger.log(logMessage, Logger.Level.DEBUG);
    }
}

