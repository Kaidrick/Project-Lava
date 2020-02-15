package ofs.backend.core.request.server;

import ofs.backend.core.request.RequestToServer;

public class ServerFillerRequest extends RequestToServer {
    {
        handle = Handle.EMPTY;
        port = 3011;
    }

    @Override
    public void resolve(String object) {

    }
}
