package moe.ofs.backend.request;

public class RequestInvalidStateException extends RuntimeException {
    public RequestInvalidStateException() {
        super();
    }

    public RequestInvalidStateException(String message) {
        super(message);
    }
}
