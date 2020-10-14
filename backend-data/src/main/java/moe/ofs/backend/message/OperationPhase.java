package moe.ofs.backend.message;

public enum OperationPhase {
    PREPARING, LOADING, STOPPING, IDLE, RUNNING;

    public int getStatusCode() {
        return this.ordinal();
    }

    public String getStatusString() {
        return this.name().toLowerCase();
    }
}
