package moe.ofs.backend.discipline.aspects;

public enum NetAction {
    CONNECT(0), DISCONNECT(1), CHANGE_SLOT(2),
    BANNED(3), KICKED(4), CHAT_MESSAGE(5), CHAT_COMMAND(6);

    private final int actionCode;

    NetAction(int actionCode) {
        this.actionCode = actionCode;
    }

    public int getActionCode() {
        return actionCode;
    }

    public String getActionName() {
        return name().toLowerCase().replace("_", "-");
    }
}
