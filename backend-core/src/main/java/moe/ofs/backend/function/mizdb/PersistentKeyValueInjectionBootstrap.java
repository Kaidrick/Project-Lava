package moe.ofs.backend.function.mizdb;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.handlers.LuaScriptInjectionObservable;
import moe.ofs.backend.handlers.starter.LuaScriptStarter;
import moe.ofs.backend.handlers.starter.ScriptInjectionTask;
import moe.ofs.backend.request.DataRequest;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class PersistentKeyValueInjectionBootstrap implements LuaScriptStarter {

//    @PostConstruct
//    private void register() {
//        LuaScriptInjectionObservable luaScriptInjectionObservable = this::databaseInit;
//        luaScriptInjectionObservable.register();
//    }
//
//    private void databaseInit() {
//        LuaScripts.requestWithFile(LuaQueryEnv.MISSION_SCRIPTING, "storage/mission/init_keyvalue.lua");
//        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL, "storage/server/init_keyvalue.lua");
//
//        log.info("Persistent Key-Value Storage Initialized");
//    }

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
