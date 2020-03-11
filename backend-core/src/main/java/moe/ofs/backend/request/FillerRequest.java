package moe.ofs.backend.request;

import moe.ofs.backend.domain.Handle;
import moe.ofs.backend.domain.Level;

public class FillerRequest extends BaseRequest {

    public FillerRequest(Level level) {

        super(level);

        handle = Handle.EMPTY;

    }
}
