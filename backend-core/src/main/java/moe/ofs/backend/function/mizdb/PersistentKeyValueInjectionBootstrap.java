package moe.ofs.backend.function.mizdb;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.handlers.starter.LuaScriptStarter;
import moe.ofs.backend.handlers.starter.model.ScriptInjectionTask;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PersistentKeyValueInjectionBootstrap implements LuaScriptStarter {

    @Override
    public ScriptInjectionTask injectScript() {
        return ScriptInjectionTask.builder()
                .inject(() ->
                        LuaScripts.requestWithFile(LuaQueryEnv.MISSION_SCRIPTING,
                                "storage/mission/init_keyvalue.lua").getAsBoolean() &&
                        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                                "storage/server/init_keyvalue.lua").getAsBoolean())
                .injectionDoneCallback(success -> {
                    if (success) log.info("Persistent Key-Value Storage Initialized");
                    else log.error("Failed to initialize Persistent Key-Value Storage");
                })
                .initializrClass(getClass())
                .scriptIdentName("LavaMissionKeyValueStorage")
                .build();
    }
}
