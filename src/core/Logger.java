package core;

import main.BackendMainController;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public enum Level {
        ERROR, INFO, WARNING, EVENT, ADDON
    }

    private static String decodeUTF8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static byte[] encodeUTF8(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static void log(String string, Level level) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuu-MM-dd/HH:mm:ss");

        String logMessage = dateTimeFormatter.format(zonedDateTime) + " " +
                level + " -> " + string;

        String finalString = decodeUTF8(encodeUTF8(logMessage));

        javafx.application.Platform.runLater(() -> BackendMainController.textArea.appendText(finalString + "\n"));
    }

    public static void log(String string) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM.dd/HH:mm:ss");

        String logMessage = dateTimeFormatter.format(zonedDateTime) + " " +
                Level.INFO + " -> " + string;

        String finalString = decodeUTF8(encodeUTF8(logMessage));

        javafx.application.Platform.runLater(() -> BackendMainController.textArea.appendText(finalString + "\n"));
    }
}
