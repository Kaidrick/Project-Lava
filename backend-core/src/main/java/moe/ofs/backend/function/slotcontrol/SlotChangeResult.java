package moe.ofs.backend.function.slotcontrol;

public class SlotChangeResult {
    private boolean allowed;
    private String reason;

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public SlotChangeResult(boolean allowed) {
        this.allowed = allowed;
    }

    public SlotChangeResult(boolean allowed, String reason) {
        this(allowed);
        this.reason = reason;
    }
}
