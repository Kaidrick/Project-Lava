package moe.ofs.backend.hookinterceptor;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public enum HookType {
    @SerializedName("onPlayerTryChangeSlot")
    ON_PLAYER_TRY_CHANGE_SLOT("onPlayerTryChangeSlot", new String[]{"playerID" , "side", "slotID"}, true, 1),

    @SerializedName("onPlayerTrySendChat")
    ON_PLAYER_TRY_SEND_CHAT("onPlayerTrySendChat", new String[]{"playerID", "msg", "all"}, true, 1),

    @SerializedName("onPlayerTryConnect")
    ON_PLAYER_TRY_CONNECT("onPlayerTryConnect", new String[]{"addr", "name", "ucid", "playerID"}, true, 4);

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
            return String.join(",", functionArgs);
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
