package moe.ofs.backend.system;

import org.springframework.stereotype.Component;

@Component
public class FrontendStatusMonitor {
    public enum Status {
        CONNECTED, DISCONNECTED
    }

    private Status status;

    public Status getStatus() {
        return status;
    }

    public void changeStatus(Status status) {
        this.status = status;
    }
}
