package moe.ofs.backend.message;

import lombok.Getter;
import moe.ofs.backend.message.connection.ConnectionStatus;

import java.time.Instant;

@Getter
public final class ConnectionStatusChange {
    private final Instant timestamp;
    private final ConnectionStatus status;

    public ConnectionStatusChange(ConnectionStatus status) {
        timestamp = Instant.now();
        this.status = status;
    }
}
