package moe.ofs.backend.hookinterceptor;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public enum HookType {
    @SerializedName("onPlayerTryChangeSlot")
    ON_PLAYER_TRY_CHANGE_SLOT("onPlayerTryChangeSlot", new String[]{"playerID" , "side", "slotID"}, true, 1),

    @SerializedName("onPlayerTrySendChat")
    ON_PLAYER_TRY_SEND_CHAT("onPlayerTrySendChat", new String[]{"playerID", "msg", "all"}, true, 1),

    @SerializedName("onPlayerTryConnect")
    ON_PLAYER_TRY_CONNECT("onPlayerTryConnect", new String[]{"addr", "name", "ucid", "playerID"}, true, 4),

    @SerializedName("onPlayerConnect")
    ON_PLAYER_CONNECT("onPlayerConnect", new String[]{"id"}, false, -1),

    @SerializedName("onPlayerDisconnect")
    ON_PLAYER_DISCONNECT("onPlayerDisconnect", new String[]{"id", "err_code"}, false, -1),

    @SerializedName("onPlayerStart")
    ON_PLAYER_START("onPlayerStart", new String[]{"id"}, false, -1),

    @SerializedName("onPlayerStop")
    ON_PLAYER_STOP("onPlayerStop", new String[]{"id"}, false, -1),

    @SerializedName("onPlayerChangeSlot")
    ON_PLAYER_CHANGE_SLOT("onPlayerChangeSlot", new String[]{"id"}, false, -1),

    @SerializedName("onGameEvent")
    ON_GAME_EVENT("onGameEvent", new String[]{"eventName", "..."}, false, -1);


    private final String functionName;
    private final String[] functionArgs;
    private final boolean interceptAllowed;
    private final int playerNetIdArgIndex;

    public boolean isInterceptAllowed() {
        return interceptAllowed;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String[] getFunctionArgs() {
        return functionArgs;
    }

    public int getPlayerNetIdArgIndex() {
        return playerNetIdArgIndex;
    }

    public String getFunctionArgsString() {
        return getFunctionArgsString("storage");
    }

    public String getFunctionArgsString(String storageArgName) {
        if (storageArgName == null) {
            return "_," +  String.join(",", functionArgs);
        }
        return storageArgName + "," + String.join(",", functionArgs);
    }

    public static HookType ofFunctionName(String functionName) {
        return Arrays.stream(HookType.values())
                .filter(h -> h.functionName.equals(functionName)).findAny()
                .orElseThrow(() -> new RuntimeException("Unknown hook target function name"));
    }

    HookType(String functionName, String[] functionArgs, boolean interceptAllowed, int playerNetIdArgIndex) {
        this.functionName = functionName;
        this.functionArgs = functionArgs;
        this.interceptAllowed = interceptAllowed;
        this.playerNetIdArgIndex = playerNetIdArgIndex;
    }
}
