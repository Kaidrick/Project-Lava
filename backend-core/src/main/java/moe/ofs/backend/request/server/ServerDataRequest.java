package moe.ofs.backend.request.server;

import com.google.gson.Gson;
import moe.ofs.backend.domain.Handle;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.request.Processable;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.Resolvable;

import java.util.ArrayList;
import java.util.List;

public class ServerDataRequest extends RequestToServer implements Resolvable {

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

    /**
     * blocking call
     * @return result
     */
    public String get() {
        if(!isSent()) {
            send();
        }
        while(true) {
            if(result != null) {
                return result;
            }
        }
    }

    // TODO --> generify server data request, see ConnectionManager for detail
    public <T> T getAs(Class<T> type) {
        Gson gson = new Gson();
        if(!isSent()) {
            send();
        }
        return gson.fromJson(get(), type);
    }

    public long getAsLong() {
        if(!isSent()) {
            send();
        }
        return Long.parseLong(get());
    }

    public int getAsInt() {
        if(!isSent()) {
            send();
        }
        return Integer.parseInt(get());
    }

    public double getAsDouble() {
        if(!isSent()) {
            send();
        }
        return Double.parseDouble(get());
    }

    public boolean getAsBoolean() {
        if(!isSent()) {
            send();
        }
        return Boolean.parseBoolean(get());
    }

    public ServerDataRequest addProcessable(Processable processable) {
        list.add(processable);
        return this;
    }

    public void notifyProcessable(String object) {
        list.forEach(p -> p.process(object));
    }
}
