package moe.ofs.backend.logmanager;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    public static void log(Level level, String string) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd/HH:mm:ss");

        LogEntry logEntry = LogEntry.builder()
                .level(level)
                .message(string)
                .time(zonedDateTime).build();

        javafx.application.Platform.runLater(() -> LogAppendedEventHandler.invokeAll(logEntry));
    }

    public static void log(String string) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd/HH:mm:ss");

        LogEntry logEntry = LogEntry.builder()
                .level(Level.INFO)
                .message(string)
                .time(zonedDateTime).build();

        javafx.application.Platform.runLater(() -> LogAppendedEventHandler.invokeAll(logEntry));
    }

    public static void debug(String message) {
        log(Level.DEBUG, message);
    }

    public static void addon(String message) {
        log(Level.ADDON, message);
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void error(String message) {
        log(Level.WARN, message);
    }

    public static void event(String message) {
        log(Level.EVENT, message);
    }

    public static void warn(String message) {
        log(Level.WARN, message);
    }
}
