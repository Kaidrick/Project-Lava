package moe.ofs.backend.logmanager;

public enum Level {
    ERROR("log-message-error"), INFO("log-message-info"), WARN("log-message-warn"),
    EVENT("log-message-event"), ADDON("log-message-addon"), DEBUG("log-message-debug");

    private String style;

    public String getStyle() {
        return style;
    }

    Level(String style) {
        this.style = style;
    }
}
