package moe.ofs.backend.util.lua;

import moe.ofs.backend.request.LuaResponse;
import moe.ofs.backend.request.DataRequest;
import moe.ofs.backend.util.LuaScripts;

public interface LuaQueryCapable {
    default LuaResponse query(DataRequest.State state, String luaString) {
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
            switch (luaState.value()) {
                case MISSION_SCRIPTING:
                    return query(DataRequest.State.MISSION, luaString);
                case SERVER_CONTROL:
                    return query(DataRequest.State.DEBUG, luaString);
                case EXPORT:
                    break;
                case TRIGGER:
                    break;
                default:
                    break;
            }
        }

        // default to mission scripting environment
        return query(DataRequest.State.MISSION, luaString);
    }

}
