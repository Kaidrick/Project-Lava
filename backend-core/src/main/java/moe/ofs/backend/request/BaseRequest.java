package moe.ofs.backend.request;

import com.google.gson.Gson;
import moe.ofs.backend.domain.Handle;
import moe.ofs.backend.domain.Level;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class BaseRequest {

    protected int port;
    protected Level level;
    protected Handle handle;
    protected List<Object> params = new ArrayList<>();
    protected UUID uuid;

    private transient boolean isSent = false;

    private static class RequestBadStateException extends RuntimeException {
        RequestBadStateException() {
            super("Request cannot be sent more than once.");
        }
    }

    public BaseRequest(Level level) {
        this.level = level;
        this.port = level.getPort();
        this.uuid = UUID.randomUUID();
    }

    public int getPort() {
        return port;
    }

    public Level getLevel() {
        return level;
    }

    public String getUuid() {
        return uuid.toString();
    }

    public JsonRpcRequest toJsonRpcCall() {
        prepareParameters();
        return new JsonRpcRequest (uuid, handle.name(), params);
    }


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

    public BaseRequest send() {
        if(isSent) {
            throw new RequestBadStateException();
        } else {
            prepareParameters();
            RequestHandler.getInstance().take(this);
            isSent = true;
        }
        return this;
    }
}
