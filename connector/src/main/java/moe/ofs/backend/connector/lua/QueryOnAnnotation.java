package moe.ofs.backend.connector.lua;

import moe.ofs.backend.connector.response.LuaResponse;
import moe.ofs.backend.connector.util.LuaScripts;

public class QueryOnAnnotation {
    protected LuaResponse query(String debugString) {
        Environment environment = this.getClass().getAnnotation(InjectionEnvironment.class).value();
        switch (environment) {
            case MISSION:
                return LuaScripts.request(LuaQueryEnv.MISSION_SCRIPTING, debugString);
            case HOOK:
                return LuaScripts.request(LuaQueryEnv.SERVER_CONTROL, debugString);
            case EXPORT:
                throw new RuntimeException("EXPORT NOT IMPLEMENTED");
            case TRIGGER:
                throw new RuntimeException("TRIGGER NOT IMPLEMENTED");
            default:
                throw new RuntimeException();
        }
    }

    protected LuaResponse queryWithFile(String scriptName, Object[] args) {
        String debugString = LuaScripts.loadAndPrepare(scriptName, args);
        return query(debugString);
    }

    protected void execute(String debugString) {
        Environment environment = this.getClass().getAnnotation(InjectionEnvironment.class).value();
        switch (environment) {
            case MISSION:
                LuaScripts.request(LuaQueryEnv.MISSION_SCRIPTING, debugString);
                break;
            case HOOK:
                LuaScripts.request(LuaQueryEnv.SERVER_CONTROL, debugString);
                break;
            case EXPORT:
                throw new RuntimeException("EXPORT NOT IMPLEMENTED");
            case TRIGGER:
                throw new RuntimeException("TRIGGER NOT IMPLEMENTED");
            default:
                throw new RuntimeException();
        }
    }

    protected void executeWithFile(String scriptName, Object[] args) {
        String debugString = LuaScripts.loadAndPrepare(scriptName, args);
        execute(debugString);
    }
}
