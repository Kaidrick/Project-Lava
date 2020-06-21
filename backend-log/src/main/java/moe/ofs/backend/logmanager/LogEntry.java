package moe.ofs.backend.logmanager;

import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class LogEntry implements Serializable {
    private ZonedDateTime time;
    private Level level;
    private String message;
    private String source;

    @Override
    public String toString() {
        return String.format("%s %s %s",
                time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                level.name(),
                message);
    }
}
