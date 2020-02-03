package core.request;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;


public abstract class BaseRequest {

    public enum Level {
        MISSION_POLL(3008), MISSION(3009),
        SERVER(3010), SERVER_POLL(3011),
        EXPORT(3012), EXPORT_POLL(3013);

        private int port;

        Level(int port) {
            this.port = port;
        }

        public int getPort() {
            return port;
        }
    }

    public enum Handle {
        MESSAGE, QUERY, DEBUG, EXEC, EMPTY
    }

    private static class RequestBadStateException extends RuntimeException {
        RequestBadStateException() {
            super("Request cannot be sent more than once.");
        }
    }

    private static class RequestPreparedParameterRepeatedException extends RuntimeException {
        RequestPreparedParameterRepeatedException() {
            super("Request parameter cannot be prepared more than once.");
        }
    }

    protected int port;
    protected Handle handle;
    protected List<Object> params = new ArrayList<>();

    private transient boolean isSent = false;

    BaseRequest(Level level) {
        this.port = level.getPort();
        this.uuid = UUID.randomUUID();
    }

    protected UUID uuid;

    public int getPort() {
        return port;
    }

    public String getUuid() {
        return uuid.toString();
    }

    public JsonRpcRequest jsonRpcRequest;

    public JsonRpcRequest toJsonRpcCall() {
        prepareParameters();
        return new JsonRpcRequest (uuid, handle.name(), params);
    }

//    public static BaseRequest getFillerInstance() {
//        return new BaseRequest(Level.SERVER) {
//            @Override
//            public void resolve(String object) {
//
//            }
//        };
//    }

    public void prepareParameters() {
        if(isSent) {
            return;
        }

        Field[] fields = this.getClass().getDeclaredFields();

        Consumer<String> addField = s -> {
            try {
                Field field = this.getClass().getDeclaredField(s);
                field.setAccessible(true);
                this.params.add(field.get(this));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        };

        Arrays.stream(fields)
                .filter(f -> {
                    String modifiers = Modifier.toString(f.getModifiers());
                    String fieldName = f.getName();
//                    System.out.println(fieldName);
                    return modifiers.contains("transient") && !fieldName.equals("isSent");
                })
                .map(Field::getName)
                .forEach(addField);
    }

    @Override
    public String toString() {
        prepareParameters();
        Gson gson = new Gson();
        return super.toString() + "|" + gson.toJson(this);
    }

    public void send() {
        if(isSent) {
            throw new RequestBadStateException();
        } else {
            prepareParameters();
            RequestHandler.getInstance().take(this);
            isSent = true;
        }

    }

    public abstract void resolve(String object);

}




