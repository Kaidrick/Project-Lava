package moe.ofs.backend.request.export;

import moe.ofs.backend.request.BaseRequest;

public abstract class RequestToExport extends BaseRequest {
    RequestToExport() {
        super(Level.EXPORT);
    }
}
