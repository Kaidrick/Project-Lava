package moe.ofs.backend.connector.exceptions;

public class RequestInvalidStateException extends RuntimeException {
    public RequestInvalidStateException() {
        super();
    }

    public RequestInvalidStateException(String message) {
        super(message);
    }
}
