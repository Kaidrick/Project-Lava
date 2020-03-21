package moe.ofs.backend.logmanager;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class LogEntry {
    private ZonedDateTime time;
    private Level level;
    private String message;
}
