package moe.ofs.backend.function;

import java.io.Serializable;

public class Message implements Serializable {
    private int duration;
    private String content;
    private String prefix;
    private String suffix;
    private int interval;

    private Message() {}

    public Message(String content) {
        this.content = content;
        this.duration = 5;
    }

    public Message(String content, int duration) {
        this(content);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public String getContent() {
        return content;
    }

    public int getWaitNextMessage() {
        return interval + duration;
    }

    public String toString() {
        return content;
    }
}
