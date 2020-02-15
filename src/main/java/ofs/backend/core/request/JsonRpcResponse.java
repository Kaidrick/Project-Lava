package ofs.backend.core.request;

public class JsonRpcResponse<T> {

    private class Error {
        private int code;
        private String message;
    }

    private String jsonrpc;
    private Result<T> result;
    private Error error;
    private String id;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public Result<T> getResult() {
        return result;
    }

    public Error getError() {
        return error;
    }

    public String getId() {
        return id;
    }
}
