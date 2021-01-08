package moe.ofs.backend.connector.request;

import moe.ofs.backend.domain.connector.Level;

public class FillerRequest extends BaseRequest {

    public FillerRequest(Level level) {

        super(level);

        handle = Handle.EMPTY;

    }
}
