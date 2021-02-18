package moe.ofs.backend.domain.message.connection;

import lombok.Getter;
import moe.ofs.backend.domain.message.connection.ConnectionStatus;

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
