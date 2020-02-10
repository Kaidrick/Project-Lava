package core.request.server;

import core.request.RequestToServer;

public class ServerEnvRequest extends RequestToServer {
    {
        handle = Handle.EXEC;
        port = 3010;
        state = State.DEBUG;
    }

    @Override
    public void resolve(String object) {
        System.out.println(object);
    }
}
