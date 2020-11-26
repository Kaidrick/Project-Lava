package moe.ofs.backend.simevent.services;

import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.mizdb.AbstractMissionDataService;
import moe.ofs.backend.simevent.model.SimEvent;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class SimEventPollServiceImpl extends AbstractMissionDataService<SimEvent> implements SimEventPollService {

    public SimEventPollServiceImpl(RequestTransmissionService requestTransmissionService) {
        super(requestTransmissionService);
    }



    @Override
    public void poll() throws IOException {

    }

    @Override
    @PostConstruct
    public void init() {
        MissionStartObservable missionStartObservable = s -> createRepository();
        missionStartObservable.register();
    }

    @Override
    public String getRepositoryName() {
        return this.getClass().getName();
    }
}
