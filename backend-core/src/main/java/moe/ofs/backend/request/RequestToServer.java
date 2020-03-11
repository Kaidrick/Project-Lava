package moe.ofs.backend.request;

import moe.ofs.backend.domain.Level;

public abstract class RequestToServer extends BaseRequest {

    protected State state;

    public enum State {
        SERVER, CONFIG, MISSION, EXPORT, DEBUG
    }

    public RequestToServer(Level level) {
        super(level);
    }
}
