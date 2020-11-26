package moe.ofs.backend.simevent.services;

import moe.ofs.backend.services.map.AbstractPageableMapService;
import moe.ofs.backend.simevent.model.SimEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SimEventServiceImpl extends AbstractPageableMapService<SimEvent>
        implements SimEventService {
    @Override
    public void broadcast(SimEvent event) {

    }

    @Scheduled(fixedDelay = 1000L)
    private void gather() {

    }


}
