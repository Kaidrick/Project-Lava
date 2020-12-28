package moe.ofs.backend.function.mizdb;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.handlers.starter.LuaScriptStarter;
import moe.ofs.backend.handlers.starter.model.ScriptInjectionTask;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PersistentStructuredDataInjectionBootstrap implements LuaScriptStarter {

    @Override
    public ScriptInjectionTask injectScript() {
        return ScriptInjectionTask.builder()
                .inject(() ->
                        LuaScripts.requestWithFile(LuaQueryEnv.MISSION_SCRIPTING,
                                "storage/mission/init.lua").getAsBoolean() &&
                        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                                "storage/server/init.lua").getAsBoolean())
                .injectionDoneCallback(success -> {
                    if (success) log.info("Persistent Structured Storage Initialized");
                    else log.error("Failed to initialize Persistent Structured Storage");
                })
                .initializrClass(getClass())
                .scriptIdentName("LavaMissionStructuredStorage")
                .build();
    }
}
