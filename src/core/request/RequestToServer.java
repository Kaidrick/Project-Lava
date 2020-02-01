package core.request;

public abstract class RequestToServer extends BaseRequest {

    protected State state;

    public enum State {
        SERVER, CONFIG, MISSION, EXPORT
    }

    public RequestToServer() {
        super(Level.SERVER);
    }
}
