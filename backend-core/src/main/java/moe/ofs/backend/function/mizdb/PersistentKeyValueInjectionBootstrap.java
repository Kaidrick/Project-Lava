package moe.ofs.backend.function.mizdb;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.handlers.LuaScriptInjectionObservable;
import moe.ofs.backend.request.DataRequest;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class PersistentKeyValueInjectionBootstrap {

    @PostConstruct
    private void register() {
        LuaScriptInjectionObservable luaScriptInjectionObservable = this::databaseInit;
        luaScriptInjectionObservable.register();
    }

    private void databaseInit() {
        LuaScripts.requestWithFile(DataRequest.State.SERVER, "storage/mission/init_keyvalue.lua");
        LuaScripts.requestWithFile(DataRequest.State.DEBUG, "storage/server/init_keyvalue.lua");

        log.info("Persistent Key-Value Storage Initialized");
    }
}
