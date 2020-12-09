package moe.ofs.backend.request.server;

import moe.ofs.backend.domain.Handle;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.request.Processable;
import moe.ofs.backend.request.DataRequest;
import moe.ofs.backend.util.lua.LuaQueryEnv;

import java.util.ArrayList;
import java.util.List;

public class ServerActionRequest extends DataRequest {
    {
        handle = Handle.EXEC;
        port = 3010;
        state = LuaQueryEnv.MISSION_SCRIPTING;
    }

    private volatile String result;
    private List<Processable> list = new ArrayList<>();

    private transient String env;
    private transient String luaString;


    public ServerActionRequest(String luaString) {
        super(Level.SERVER);

        this.luaString = luaString;
        this.env = this.state.getEnv().toLowerCase();
    }

    public ServerActionRequest(LuaQueryEnv state, String luaString) {
        super(Level.SERVER);

        this.luaString = luaString;
        this.env = state.getEnv().toLowerCase();
    }
}
