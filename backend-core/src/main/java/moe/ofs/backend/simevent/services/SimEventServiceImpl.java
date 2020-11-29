package moe.ofs.backend.simevent.services;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.BackgroundTask;
import moe.ofs.backend.domain.LavaEvent;
import moe.ofs.backend.message.OperationPhase;
import moe.ofs.backend.services.map.AbstractPageableMapService;
import moe.ofs.backend.domain.SimEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SimEventServiceImpl extends AbstractPageableMapService<SimEvent>
        implements SimEventService {

    @Value("${lava.event.association-retry-limit:20}")
    private int retryCountLimit;

    private final SimEventRegistryService simEventRegistryService;
    private final SimEventPollService simEventPollService;

    private final List<SimEvent> limbo;

    public SimEventServiceImpl(SimEventRegistryService simEventRegistryService, SimEventPollService simEventPollService) {
        this.simEventRegistryService = simEventRegistryService;
        this.simEventPollService = simEventPollService;

        this.limbo = new ArrayList<>();
    }

    @Override
    public void broadcast(LavaEvent event) {
        System.out.println("event = " + event);
    }

    @Scheduled(fixedDelay = 200L)
    private void gather() throws IOException {
        if (BackgroundTask.getCurrentTask().getPhase().equals(OperationPhase.RUNNING)) {
            // always try unfinished association first
            limbo.removeIf(event -> event.getAssociateRetryCount() <= retryCountLimit);
            limbo.forEach(simEventRegistryService::associate);

            Map<Boolean, List<SimEvent>> map = simEventPollService.poll().parallelStream()
                    .filter(simEvent -> simEvent.getInitiatorId() != 0)
                    .map(simEventRegistryService::associate)
                    .collect(Collectors.groupingBy(SimEvent::isAssociated));

            if (map.containsKey(true)) {
                map.get(true).stream().map(event -> (LavaEvent) event).forEach(this::broadcast);
            }

            if (map.containsKey(false)) {
                limbo.addAll(map.get(false));
                log.info("{} SimEvents moving to limbo state; total limbo size: {}",
                        map.get(false).size(), limbo.size());
            }
        }
    }


}
