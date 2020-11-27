package moe.ofs.backend.simevent.services;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.mizdb.AbstractMissionDataService;
import moe.ofs.backend.simevent.model.SimEvent;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Service
public class SimEventPollServiceImpl extends AbstractMissionDataService<SimEvent> implements SimEventPollService {

    public SimEventPollServiceImpl(RequestTransmissionService requestTransmissionService) {
        super(requestTransmissionService);
    }



    @Override
    public void poll() throws IOException {
        fetchMapAll(LuaScripts.load("simevent/mapper/event_id_flat_map.lua"), SimEvent.class)
                .forEach(System.out::println);
    }

    @Override
    @PostConstruct
    public void init() {
        MissionStartObservable missionStartObservable = s -> {
            createRepository();

            requestTransmissionService.send(
                    new ServerExecRequest(LuaScripts.loadAndPrepare("simevent/simulation_event_collector.lua",
                            getRepositoryName()))
            );
        };
        missionStartObservable.register();

        log.info("Sim Event Poll Service Initialized.");
    }

    @Override
    public String getRepositoryName() {
        return this.getClass().getName();
    }
}
