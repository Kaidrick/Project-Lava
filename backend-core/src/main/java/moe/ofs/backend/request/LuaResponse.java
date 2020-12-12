package moe.ofs.backend.request;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.BackgroundTask;
import moe.ofs.backend.message.OperationPhase;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.*;

public interface LuaResponse extends Resolvable {
    boolean isSent();

    String getResult();

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
    default String get() {
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
        }
        Instant entryTime = Instant.now();
        while(true) {
            OperationPhase phase = BackgroundTask.getCurrentTask().getPhase();
            if (phase == OperationPhase.RUNNING || phase == OperationPhase.LOADING) {
                if(getResult() != null) {
                    if (getResult().isEmpty()) {
                        return "<LUA EMPTY STRING>";
                    }

                    return getResult();
                } else {
//                    System.out.println(entryTime + ", " + Instant.now());
                    if (Instant.now().minusMillis(2000).isAfter(entryTime)) {
                        return "<NO RESULT OR LUA TIMED OUT>";
                    }
                }
            } else {
                // return something that indicates bad operation phase
                LoggerFactory.getLogger(getClass()).warn("Request bad operation phase: {}",
                        BackgroundTask.getCurrentTask().getPhase().toString());
                return null;
            }

            try {
                Thread.sleep(1);  // TODO: maybe use message in the future?
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO --> generify server data request, see ConnectionManager for detail
    default <T> T getAs(Class<T> type) {
        Gson gson = new Gson();
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
        }
        return gson.fromJson(get(), type);
    }

    default <T> List<T> getAsListFor(Class<T> tClass) {
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
        }
        Type type = TypeToken.getParameterized(ArrayList.class, tClass).getType();
        return getAs(type);
    }

    default <T> Set<T> getAsSetFor(Class<T> tClass) {
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
        }
        Type type = TypeToken.getParameterized(HashSet.class, tClass).getType();
        return getAs(type);
    }

    default <T> T getAs(Type type) {
        Gson gson = new Gson();
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
        }
        return gson.fromJson(get(), type);
    }

    default long getAsLong() {
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
        }
        return Long.parseLong(get());
    }

    default int getAsInt() {
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
        }
        return Integer.parseInt(get());
    }

    default double getAsDouble() {
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
        }
        return Double.parseDouble(get());
    }

    default boolean getAsBoolean() {
        if(!isSent()) {
            throw new RequestInvalidStateException("Request has never been sent and thus has no result.");
        }
        return Boolean.parseBoolean(get());
    }
}
