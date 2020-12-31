package moe.ofs.backend.handlers.starter;

import moe.ofs.backend.handlers.starter.model.ScriptInjectionTask;

public interface LuaScriptStarter {
    ScriptInjectionTask injectScript();
}
