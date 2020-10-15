package moe.ofs.backend.function.mizdb;

import moe.ofs.backend.handlers.LuaScriptInjectionObservable;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerActionRequest;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MissionPersistenceDatabaseBootstrap {

    private final RequestTransmissionService requestTransmissionService;

    public MissionPersistenceDatabaseBootstrap(RequestTransmissionService requestTransmissionService) {
        this.requestTransmissionService = requestTransmissionService;
    }

    @PostConstruct
    private void register() {
        LuaScriptInjectionObservable luaScriptInjectionObservable = this::databaseInit;
        luaScriptInjectionObservable.register();
    }

    private void databaseInit() {
        requestTransmissionService.send(
                new ServerDataRequest(LuaScripts.load("mizdb/init_mizdb.lua"))
        );

        System.out.println("init mizdb");
    }
}
