package moe.ofs.backend.lavalog;

import moe.ofs.backend.jms.Sender;
import moe.ofs.backend.object.LogLevel;
import moe.ofs.backend.domain.LogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class LavaLog {

    public static final String TOPIC = "backend.entry";

    private static Sender sender;

    @Autowired
    public LavaLog(Sender sender) {
        LavaLog.sender = sender;
    }

    public static class Logger {
        private String source;

        public Logger() {}

        public Logger(String source) {
            this.source = source;
        }

        public void log(LogLevel logLevel, String string) {
            ZonedDateTime zonedDateTime = ZonedDateTime.now();

            LogEntry logEntry = LogEntry.builder()
                    .logLevel(logLevel)
                    .message(string)
                    .source(source)
                    .time(zonedDateTime.toString()).build();

            sender.sendToTopic(TOPIC, logEntry, null);
        }

        public void log(String string) {
            log(LogLevel.INFO, string);
        }

        public void debug(String message) {
            log(LogLevel.DEBUG, message);
        }

        public void addon(String message) {
            log(LogLevel.ADDON, message);
        }

        public void info(String message) {
            log(LogLevel.INFO, message);
        }

        public void error(String message) {
            log(LogLevel.WARN, message);
        }

        public void event(String message) {
            log(LogLevel.EVENT, message);
        }

        public void warn(String message) {
            log(LogLevel.WARN, message);
        }
    }

    public static <T> Logger getLogger(Class<T> tClass) {
        return new Logger(tClass.getName());
    }

    public Logger getLogger() {
        return new Logger();
    }
}
