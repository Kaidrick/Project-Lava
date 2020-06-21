package moe.ofs.backend.function.unitwiselog;

import moe.ofs.backend.jms.Sender;
import moe.ofs.backend.logmanager.Level;
import moe.ofs.backend.logmanager.LogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class LogControl {

    public static final String TOPIC = "backend.entry";

    private static Sender sender;

    @Autowired
    public LogControl(Sender baseSender) {
        LogControl.sender = baseSender;
    }

    public static class Logger {
        private String source;

        public Logger() {}

        public Logger(String source) {
            this.source = source;
        }

        public void log(Level level, String string) {
            ZonedDateTime zonedDateTime = ZonedDateTime.now();

            LogEntry logEntry = LogEntry.builder()
                    .level(level)
                    .message(string)
                    .source(source)
                    .time(zonedDateTime).build();

            sender.sendToTopic(TOPIC, logEntry, null);
        }

        public void log(String string) {
            log(Level.INFO, string);
        }

        public void debug(String message) {
            log(Level.DEBUG, message);
        }

        public void addon(String message) {
            log(Level.ADDON, message);
        }

        public void info(String message) {
            log(Level.INFO, message);
        }

        public void error(String message) {
            log(Level.WARN, message);
        }

        public void event(String message) {
            log(Level.EVENT, message);
        }

        public void warn(String message) {
            log(Level.WARN, message);
        }
    }

    public static <T> Logger getLogger(Class<T> tClass) {
        return new Logger(tClass.getName());
    }

    public Logger getLogger() {
        return new Logger();
    }
}
