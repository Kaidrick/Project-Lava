package moe.ofs.backend.core.request.server;

import moe.ofs.backend.core.request.RequestToServer;
import java.util.ArrayList;
import java.util.List;

public class ServerDataRequest extends RequestToServer {

    {
        handle = Handle.EXEC;
        port = 3010;
        state = State.SERVER;
    }

    private volatile String result;
    private List<Processable> list = new ArrayList<>();

    private transient String env;
    private transient String luaString;



    public ServerDataRequest(String luaString) {
        this.luaString = luaString;
        this.env = this.state.name().toLowerCase();
    }

    @Override
    public void resolve(String object) {
        this.result = object;
        notifyProcessable(object);
    }

    /**
     * blocking call
     * @return result
     */
    public String get() {
        while(true) {
            if(result != null) {
                return result;
            }
        }
    }

    public int getAsInt() {
        return Integer.parseInt(get());
    }

    public double getAsDouble() {
        return Double.parseDouble(get());
    }


    public ServerDataRequest addProcessable(Processable processable) {
        list.add(processable);
        return this;
    }

    public void notifyProcessable(String object) {
        list.forEach(p -> p.process(object));
    }
}
