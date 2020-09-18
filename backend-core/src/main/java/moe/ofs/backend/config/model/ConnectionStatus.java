package moe.ofs.backend.config.model;

import lombok.Data;
import moe.ofs.backend.OperationPhase;

import java.time.LocalDateTime;

@Data
public class ConnectionStatus {
    private LocalDateTime timestamp;
    private boolean isConnected;
    private int phaseCode;
    private String theater;
    private int objectCount;
    private int playerCount;
}
