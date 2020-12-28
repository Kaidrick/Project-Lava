package moe.ofs.backend.util.lua;

import moe.ofs.backend.request.LuaResponse;
import moe.ofs.backend.request.DataRequest;
import moe.ofs.backend.util.LuaScripts;

public interface LuaQueryCapable {
    default LuaResponse query(LuaQueryEnv state, String luaString) {
        return LuaScripts.request(state, luaString);
    }

    /**
     * If a LuaQueryState annotation is present, send request to said destination and return String;
     * otherwise, send to mission scripting environment via hook load string function.
     * @param luaString String to be executed as Lua code
     * @return String of JSON format or plain String
     */
    default LuaResponse query(String luaString) {
        LuaQueryState luaState = getClass().getAnnotation(LuaQueryState.class);
        if (luaState != null) {

            return query(luaState.value(), luaString);
        }

        // default to mission scripting environment
        return query(LuaQueryEnv.MISSION_SCRIPTING, luaString);
    }

}
