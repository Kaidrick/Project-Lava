package moe.ofs.backend.request;

import moe.ofs.backend.domain.Level;

public abstract class DataRequest extends BaseRequest {

    protected State state;

    public enum State {
        SERVER, CONFIG, MISSION, EXPORT, DEBUG
    }

    public DataRequest(Level level) {
        super(level);
    }
}
