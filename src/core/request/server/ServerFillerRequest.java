package core.request.server;

import core.request.RequestToServer;

public class ServerFillerRequest extends RequestToServer {
    {
        handle = Handle.EMPTY;
        port = 3011;
    }

    @Override
    public void resolve(String object) {

    }
}
