package core.request.server;

import core.request.RequestToServer;
import java.util.ArrayList;
import java.util.List;

public class ServerDataRequest extends RequestToServer {

    {
        handle = Handle.EXEC;
        port = 3010;
        state = State.SERVER;
    }

    private List<Processable> list = new ArrayList<>();

    private transient String env;
    private transient String luaString;

    public ServerDataRequest(String luaString) {
        this.luaString = luaString;
        this.env = this.state.name().toLowerCase();
    }

    @Override
    public void resolve(String object) {
//        System.out.println(object);
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
