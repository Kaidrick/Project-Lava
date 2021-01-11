package moe.ofs.backend.connector.request;

import moe.ofs.backend.domain.connector.Level;

public class PollRequest extends BaseRequest {
    public PollRequest(Level level) {
        super(level);
        handle = Handle.QUERY;
    }
}
