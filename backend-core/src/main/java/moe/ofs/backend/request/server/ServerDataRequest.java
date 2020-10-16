package moe.ofs.backend.request.server;

import com.google.gson.Gson;
import moe.ofs.backend.BackgroundTask;
import moe.ofs.backend.message.OperationPhase;
import moe.ofs.backend.domain.Handle;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.request.Processable;
import moe.ofs.backend.request.RequestInvalidStateException;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.Resolvable;

import java.time.Instant;
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
     *
     * FIXME: should fail fast if no connection can be made
     * There are two possibilities:
     * 1. The attempt to create a connection to DCS Lua server fail. In this case, the get() method
     *    should fail and return immediately to avoid indefinitely blocking the thread.
     * 2. The backend maintains a connection to DCS Lua server, but the DCS lua server takes too
     *    long to respond for some reason; get() method should fail and return after a set amount of time.
     *
     * TODO: how to check connection? check operation phase?
     * TODO: what if phase is ok but when request is sent operation halts?
     * TODO: if above condition is true, how to check timeout?
     * TODO: override get() method with a get(long milliseconds)
     * TODO: the default get() should wait 5 seconds by default.
     * TODO: use Optional to extinguish between empty return and null value.
     * @return result
     */
    public String get() {
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
        }
        Instant entryTime = Instant.now();
        while(true) {
            if (BackgroundTask.getCurrentTask().getPhase() == OperationPhase.RUNNING) {
                if(result != null) {
                    if (result.isEmpty()) {
                        return "<LUA EMPTY STRING>";
                    }

                    return result;
                } else {
//                    System.out.println(entryTime + ", " + Instant.now());
                    if (Instant.now().minusMillis(2000).isAfter(entryTime)) {
                        return "<NO RESULT OR LUA TIMED OUT>";
                    }
                }
            } else {
                // return something that indicates bad operation phase
                System.out.println("ServerDataRequest.get -> bad operation phase -> " +
                        BackgroundTask.getCurrentTask().getPhase().toString());
                return null;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO --> generify server data request, see ConnectionManager for detail
    public <T> T getAs(Class<T> type) {
        Gson gson = new Gson();
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
        }
        return gson.fromJson(get(), type);
    }

    public long getAsLong() {
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
        }
        return Long.parseLong(get());
    }

    public int getAsInt() {
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
        }
        return Integer.parseInt(get());
    }

    public double getAsDouble() {
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
        }
        return Double.parseDouble(get());
    }

    public boolean getAsBoolean() {
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
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
