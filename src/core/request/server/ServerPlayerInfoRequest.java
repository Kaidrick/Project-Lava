package core.request.server;

import core.request.BaseRequest;
import core.request.JsonRpcResponse;
import core.request.RequestToServer;

public class ServerPlayerInfoRequest extends RequestToServer {
    {
        state = State.SERVER;
        handle = Handle.QUERY;
    }

    @Override
    public void resolve(String object) {
        System.out.println(object);
    }
}
