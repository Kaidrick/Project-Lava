package moe.ofs.backend.core.request.server;

import moe.ofs.backend.core.request.RequestToServer;

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
