package moe.ofs.backend.request.export;

import moe.ofs.backend.domain.Handle;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.request.BaseRequest;

public class ExportResetRequest extends BaseRequest {
    public ExportResetRequest() {
        super(Level.EXPORT);

        handle = Handle.RESET;
    }
}
