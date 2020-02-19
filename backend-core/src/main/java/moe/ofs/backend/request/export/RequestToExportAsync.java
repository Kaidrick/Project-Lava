package moe.ofs.backend.request.export;

import java.util.UUID;

public abstract class RequestToExportAsync extends RequestToExport {
    protected UUID uuid;

    public RequestToExportAsync() {
        this.uuid = UUID.randomUUID();
    }

    public String getUuid() {
        return uuid.toString();
    }

    public abstract void resolve(Object object);

}
