package moe.ofs.backend.core.request;

public class SendMessageRequest extends RequestToMission {

    {
        handle = Handle.MESSAGE;
        duration = durationDefault;
        clearView = clearViewDefault;
    }

    private static final int durationDefault = 10;
    private static final boolean clearViewDefault = false;

    private transient Integer groupId;
    private transient String content;
    private transient float duration;
    private transient boolean clearView;

    public SendMessageRequest(String content) {
        this.content = content;
    }

    public SendMessageRequest(String content, int duration) {
        this(content);
        this.duration = duration;
    }

    public SendMessageRequest(String content, int duration, boolean clearView) {
        this(content, duration);
        this.clearView = clearView;
    }

    public SendMessageRequest(Integer groupId, String content, int duration, boolean clearView) {
        this(content, duration, clearView);
        this.groupId = groupId;
    }

    @Override
    public void resolve(String object) {

    }
}
