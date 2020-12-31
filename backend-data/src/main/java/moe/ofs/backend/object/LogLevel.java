package moe.ofs.backend.object;

public enum LogLevel {
    ERROR("log-message-error"), INFO("log-message-info"), WARN("log-message-warn"),
    EVENT("log-message-event"), ADDON("log-message-addon"), DEBUG("log-message-debug");

    private String style;

    public String getStyle() {
        return style;
    }

    LogLevel(String style) {
        this.style = style;
    }
}
