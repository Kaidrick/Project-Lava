package moe.ofs.backend.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ServerRestartDetail {
    private Instant restartTime;
    private String reason;
    private RestartType restartType;
}
