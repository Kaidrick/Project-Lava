package ofs.backend.core.request;

public class DebugRequest extends RequestToServer {
    {
        handle = Handle.DEBUG;
    }

    private static final int defaultResultInTriggerMessageDuration = 0;
//    private static final boolean defaultResultInTriggerMessage = false;

    private transient String luaString;
    private transient boolean isRepeatedInMessage = false;
    private transient int repeatInMessageDuration = 0;
    private transient boolean isRepeatMessageClearView = false;


    public DebugRequest() {
        this.luaString = "trigger.action.outText('" +
                "This is a test message sent by Java backend."
                + "', 10)";
    }

    @Override
    public void resolve(String object) {
        System.out.println(object);
    }

    public DebugRequest(String luaString) {
        this.luaString = luaString;
    }

    public DebugRequest(String luaString, boolean isRepeatedInMessage) {
        this(luaString);
        this.isRepeatedInMessage = isRepeatedInMessage;
    }

    public DebugRequest(String luaString, boolean isRepeatedInMessage,
                        int repeatInMessageDuration) {
        this(luaString, isRepeatedInMessage);
        this.repeatInMessageDuration = repeatInMessageDuration;
    }

    public DebugRequest(String luaString, boolean isRepeatedInMessage,
                        int repeatInMessageDuration, boolean isRepeatMessageClearView) {
        this(luaString, isRepeatedInMessage, repeatInMessageDuration);
        this.isRepeatMessageClearView = isRepeatMessageClearView;
    }
}
