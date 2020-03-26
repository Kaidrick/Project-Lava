package moe.ofs.backend.logmanager;

import lombok.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class LogEntry {
    private ZonedDateTime time;
    private Level level;
    private String message;

    @Override
    public String toString() {
        return String.format("%s %s %s",
                time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                level.name(),
                message);
    }
}
