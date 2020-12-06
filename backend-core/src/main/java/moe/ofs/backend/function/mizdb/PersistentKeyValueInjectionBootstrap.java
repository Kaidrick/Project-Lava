package moe.ofs.backend.function.mizdb;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.handlers.LuaScriptInjectionObservable;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class PersistentKeyValueInjectionBootstrap {
    private final RequestTransmissionService requestTransmissionService;

    public PersistentKeyValueInjectionBootstrap(RequestTransmissionService requestTransmissionService) {
        this.requestTransmissionService = requestTransmissionService;
    }

    @PostConstruct
    private void register() {
        LuaScriptInjectionObservable luaScriptInjectionObservable = this::databaseInit;
        luaScriptInjectionObservable.register();
    }

    private void databaseInit() {
        requestTransmissionService.send(
                new ServerDataRequest(LuaScripts.load("storage/mission/init_keyvalue.lua"))
        );

        requestTransmissionService.send(
                new ServerDataRequest(RequestToServer.State.DEBUG,
                        LuaScripts.load("storage/server/init_keyvalue.lua"))
        );

        log.info("Persistent Key-Value Storage Initialized");
    }
}
