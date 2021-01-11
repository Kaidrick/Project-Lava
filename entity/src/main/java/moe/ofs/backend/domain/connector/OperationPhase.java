package moe.ofs.backend.domain.connector;

public enum OperationPhase {
    PREPARING(0), LOADING(1), STOPPING(2), IDLE(3), RUNNING(4);

    private final int statusCode;

    OperationPhase(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusString() {
        return this.name().toLowerCase();
    }
}
