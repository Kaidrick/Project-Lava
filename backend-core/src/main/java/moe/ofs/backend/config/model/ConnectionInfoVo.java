package moe.ofs.backend.config.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConnectionInfoVo {
    private LocalDateTime timestamp;
    private boolean isConnected;
    private int phaseCode;
    private String theater;
    private int objectCount;
    private int playerCount;
}
