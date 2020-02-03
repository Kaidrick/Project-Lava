package core;

import main.BackendMain;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public enum Level {
        ERROR, INFO, WARNING, EVENT, ADDON, DEBUG
    }

    public static void log(String string, Level level) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd/HH:mm:ss");

        String logMessage = dateTimeFormatter.format(zonedDateTime) + " " +
                level + " -> " + string;

        javafx.application.Platform.runLater(() ->
                BackendMain.logController.appendLog(logMessage + "\n"));
    }

    public static void log(String string) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd/HH:mm:ss");

        String logMessage = dateTimeFormatter.format(zonedDateTime) + " " +
                Level.INFO + " -> " + string;

        javafx.application.Platform.runLater(() ->
                BackendMain.logController.appendLog(logMessage + "\n"));
    }
}
