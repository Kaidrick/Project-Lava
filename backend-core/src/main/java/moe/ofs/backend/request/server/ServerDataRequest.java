package moe.ofs.backend.request.server;

import moe.ofs.backend.domain.Handle;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.request.LuaResponse;
import moe.ofs.backend.request.Processable;
import moe.ofs.backend.request.DataRequest;
import moe.ofs.backend.request.Resolvable;

import java.util.ArrayList;
import java.util.List;

public class ServerDataRequest extends DataRequest implements Resolvable, LuaResponse {

    {
        handle = Handle.EXEC;
        port = 3010;
        state = State.SERVER;
    }

//    private volatile String result;
    private List<Processable> list = new ArrayList<>();

    private transient String env;
    private transient String luaString;


    public ServerDataRequest(String luaString) {
        super(Level.SERVER);

        this.luaString = luaString;
        this.env = this.state.name().toLowerCase();
    }

    public ServerDataRequest(State state, String luaString) {
        super(Level.SERVER);

        this.luaString = luaString;
        this.env = state.name().toLowerCase();
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
