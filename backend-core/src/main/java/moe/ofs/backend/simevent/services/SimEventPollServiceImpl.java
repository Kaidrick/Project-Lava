package moe.ofs.backend.simevent.services;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.function.mizdb.PersistentKeyValueInjectionBootstrap;
import moe.ofs.backend.function.mizdb.PersistentStructuredDataInjectionBootstrap;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.handlers.starter.LuaScriptStarter;
import moe.ofs.backend.handlers.starter.model.ScriptInjectionTask;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.mizdb.AbstractMissionDataService;
import moe.ofs.backend.domain.SimEvent;
import moe.ofs.backend.services.mizdb.Environment;
import moe.ofs.backend.services.mizdb.InjectionEnvironment;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Set;

@Slf4j
@Service
@InjectionEnvironment(value = Environment.MISSION)
public class SimEventPollServiceImpl extends AbstractMissionDataService<SimEvent>
        implements SimEventPollService, LuaScriptStarter {

    @Override
    public Set<SimEvent> poll() {
        return fetchMapAll(LuaScripts.load("simevent/mapper/event_id_flat_map.lua"), SimEvent.class);
    }

    @Override
    public String getRepositoryName() {
        return this.getClass().getName();
    }

    @Override
    public ScriptInjectionTask injectScript() {
        return ScriptInjectionTask.builder()
                .scriptIdentName("SimEventPollServiceScript")
                .initializrClass(getClass())
                .dependencyInitializrClass(PersistentStructuredDataInjectionBootstrap.class)
                .inject(() -> {
                    createRepository();
                    return LuaScripts.requestWithFile(LuaQueryEnv.MISSION_SCRIPTING,
                            "simevent/simulation_event_collector.lua", getRepositoryName())
                            .getAsBoolean();
                })
                .build();
    }
}
