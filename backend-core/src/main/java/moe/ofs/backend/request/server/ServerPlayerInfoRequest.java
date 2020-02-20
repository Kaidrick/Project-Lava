package moe.ofs.backend.request.server;

import moe.ofs.backend.request.RequestToServer;

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
