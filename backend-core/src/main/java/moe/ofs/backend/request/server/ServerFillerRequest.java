package moe.ofs.backend.request.server;

import moe.ofs.backend.request.RequestToServer;

public class ServerFillerRequest extends RequestToServer {
    {
        handle = Handle.EMPTY;
        port = 3011;
    }

    @Override
    public void resolve(String object) {

    }
}
