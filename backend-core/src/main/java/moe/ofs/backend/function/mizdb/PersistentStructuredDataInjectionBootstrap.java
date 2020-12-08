package moe.ofs.backend.function.mizdb;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.handlers.LuaScriptInjectionObservable;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerActionRequest;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class PersistentStructuredDataInjectionBootstrap {

    private final RequestTransmissionService requestTransmissionService;

    public PersistentStructuredDataInjectionBootstrap(RequestTransmissionService requestTransmissionService) {
        this.requestTransmissionService = requestTransmissionService;
    }

    @PostConstruct
    private void register() {
        LuaScriptInjectionObservable luaScriptInjectionObservable = this::databaseInit;
        luaScriptInjectionObservable.register();
    }

    private void databaseInit() {
//        requestTransmissionService.send(
//                new ServerDataRequest(LuaScripts.load("mizdb/init_mizdb.lua"))
//        );

        requestTransmissionService.send(
                new ServerDataRequest(LuaScripts.load("storage/mission/init.lua"))
        );

        requestTransmissionService.send(
                new ServerDataRequest(RequestToServer.State.DEBUG,
                        LuaScripts.load("storage/server/init.lua"))
        );

        log.info("Persistent Structured Storage Initialized");
    }
}
