package moe.ofs.backend.request;

public abstract class RequestToServer extends BaseRequest {

    public State state;

    public enum State {
        SERVER, CONFIG, MISSION, EXPORT, DEBUG
    }

    public RequestToServer() {
        super(Level.SERVER);
    }
}
