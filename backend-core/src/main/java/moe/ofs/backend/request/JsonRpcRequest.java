package moe.ofs.backend.request;

import java.util.UUID;

public final class JsonRpcRequest {
    private final String jsonrpc = "2.0";
    private final String method;
    private final Object params;  // a number, a list, or a map?
    private final UUID id;

    public <T> JsonRpcRequest(UUID id, String method, T params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }

    @Override
    public String toString() {
        return super.toString() + "|" + String.format("method: %s, params: %s", method, params);
    }
}
