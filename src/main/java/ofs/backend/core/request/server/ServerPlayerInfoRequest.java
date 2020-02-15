package ofs.backend.core.request.server;

import ofs.backend.core.request.RequestToServer;

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
