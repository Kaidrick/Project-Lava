package moe.ofs.backend.connector.response;

import moe.ofs.backend.domain.request.Result;

public class JsonRpcResponse<T> {

    private String jsonrpc;
    private Result<T> result;
    private JsonRpcError error;
    private String id;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public Result<T> getResult() {
        return result;
    }

    public JsonRpcError getError() {
        return error;
    }

    public String getId() {
        return id;
    }
}
