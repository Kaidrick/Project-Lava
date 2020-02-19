package moe.ofs.backend.core.request;

public abstract class RequestToServer extends BaseRequest {

    protected State state;

    public enum State {
        SERVER, CONFIG, MISSION, EXPORT, DEBUG
    }

    public RequestToServer() {
        super(Level.SERVER);
    }
}
