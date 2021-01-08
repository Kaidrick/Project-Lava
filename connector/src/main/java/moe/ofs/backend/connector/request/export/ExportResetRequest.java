package moe.ofs.backend.connector.request.export;

import moe.ofs.backend.connector.request.Handle;
import moe.ofs.backend.domain.connector.Level;
import moe.ofs.backend.connector.request.BaseRequest;

public class ExportResetRequest extends BaseRequest {
    public ExportResetRequest() {
        super(Level.EXPORT);

        handle = Handle.RESET;
    }
}
