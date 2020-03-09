package moe.ofs.backend.request;

public enum Level {
//    MISSION_POLL(3008), MISSION(3009),
    SERVER(3010), SERVER_POLL(3011),
    EXPORT(3012), EXPORT_POLL(3013);

    private int port;

    Level(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
