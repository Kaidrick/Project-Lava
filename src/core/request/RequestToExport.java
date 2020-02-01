package core.request;

public abstract class RequestToExport extends BaseRequest {
    RequestToExport() {
        super(Level.EXPORT);
    }
}
