package moe.ofs.backend.domain;

public enum Level {

    SERVER(30100), SERVER_POLL(30110),
    EXPORT(30120), EXPORT_POLL(30130);

    private int port;

    Level(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
