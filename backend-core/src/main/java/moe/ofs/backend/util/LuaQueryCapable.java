package moe.ofs.backend.util;

import moe.ofs.backend.request.RequestToServer;

public interface LuaQueryCapable {
    default void query(RequestToServer.State state, String luaString) {
        // check for annotation
        LuaScripts.request(state, luaString);
    }

}
