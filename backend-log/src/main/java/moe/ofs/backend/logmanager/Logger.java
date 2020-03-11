package moe.ofs.backend.logmanager;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public enum Level {
        ERROR, INFO, WARNING, EVENT, ADDON, DEBUG;
    }

    public static void log(String string, Level level) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd/HH:mm:ss");

        String logMessage = dateTimeFormatter.format(zonedDateTime) + " " +
                level + " " + string + "\n";

        javafx.application.Platform.runLater(() -> LogAppendedEventHandler.invokeAll(logMessage));
    }

    public static void log(String string) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd/HH:mm:ss");

        String logMessage = dateTimeFormatter.format(zonedDateTime) + " " +
                Level.INFO + " " + string + "\n";

        javafx.application.Platform.runLater(() -> LogAppendedEventHandler.invokeAll(logMessage));
    }
}
