package moe.ofs.backend.connector.request.server;

import moe.ofs.backend.connector.request.Handle;
import moe.ofs.backend.domain.connector.Level;
import moe.ofs.backend.connector.response.LuaResponse;
import moe.ofs.backend.connector.response.Processable;
import moe.ofs.backend.connector.request.DataRequest;
import moe.ofs.backend.connector.response.Resolvable;
import moe.ofs.backend.connector.lua.LuaQueryEnv;

import java.util.ArrayList;
import java.util.List;

public class ServerDataRequest extends DataRequest implements Resolvable, LuaResponse {

    {
        handle = Handle.EXEC;
        port = 3010;
        state = LuaQueryEnv.MISSION_SCRIPTING;
    }

//    private volatile String result;
    private List<Processable> list = new ArrayList<>();

    private transient String env;
    private transient String luaString;


    public ServerDataRequest(String luaString) {
        super(Level.SERVER);

        this.luaString = luaString;
        this.env = this.state.getEnv().toLowerCase();
    }

    public ServerDataRequest(LuaQueryEnv state, String luaString) {
        super(Level.SERVER);

        this.luaString = luaString;
        this.env = state.getEnv().toLowerCase();
    }

    @Override
    public void resolve(String object) {
        this.result = object;
        notifyProcessable(object);
    }

    public ServerDataRequest addProcessable(Processable processable) {
        list.add(processable);
        return this;
    }

    public void notifyProcessable(String object) {
        list.forEach(p -> p.process(object));
    }
}
