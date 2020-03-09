package moe.ofs.backend.request;

public class PollRequest extends BaseRequest {
    public PollRequest(Level level) {
        super(level);
        handle = Handle.QUERY;
    }
}
