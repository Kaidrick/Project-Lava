package moe.ofs.backend.hookinterceptor;

public enum HookType {
    ON_PLAYER_TRY_CHANGE_SLOT("onPlayerTryChangeSlot", new String[]{"playerID" , "side", "slotID"}, true),
    ON_PLAYER_TRY_SEND_CHAT("onPlayerTrySendChat", new String[]{"playerID", "msg", "all"}, true),
    ON_PLAYER_TRY_CONNECT("onPlayerTryConnect", new String[]{"addr", "name", "ucid", "playerID"}, true);

    private final String functionName;
    private final String[] functionArgs;
    private final boolean interceptAllowed;

    public boolean isInterceptAllowed() {
        return interceptAllowed;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String[] getFunctionArgs() {
        return functionArgs;
    }

    public String getFunctionArgsString() {
        return String.join(", ", functionArgs);
    }

    HookType(String functionName, String[] functionArgs, boolean interceptAllowed) {
        this.functionName = functionName;
        this.functionArgs = functionArgs;
        this.interceptAllowed = interceptAllowed;
    }
}
