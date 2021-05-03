package moe.ofs.backend.connector.request;

import moe.ofs.backend.connector.response.JsonRpcRequest;
import moe.ofs.backend.domain.connector.Level;

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

    private transient boolean sent;

    protected transient volatile String result;

    public String getResult() {
        return result;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean isSent() {
        return sent;
    }

    public List<Object> getParams() {
        return params;
    }

    protected BaseRequest(Level level) {
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

    public String getUuidString() {
        return uuid.toString();
    }

    public UUID getUuid() {
        return uuid;
    }

    public Handle getHandle() {
        return handle;
    }

    public JsonRpcRequest toJsonRpcCall() {
        prepareParameters();
        return new JsonRpcRequest (uuid, handle.name(), params);
    }


    public void prepareParameters() {
        if(sent) {
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
                    return modifiers.contains("transient") &&
                            !fieldName.equals("sent") && !fieldName.equals("result");
                })
                .map(Field::getName)
                .forEach(addField);
    }
}
