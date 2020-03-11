package moe.ofs.backend.request;

import moe.ofs.backend.domain.Handle;
import moe.ofs.backend.domain.Level;

public class PollRequest extends BaseRequest {
    public PollRequest(Level level) {
        super(level);
        handle = Handle.QUERY;
    }
}
