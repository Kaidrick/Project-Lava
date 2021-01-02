package moe.ofs.backend.simevent.services;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.LavaEvent;
import moe.ofs.backend.domain.SimEvent;
import moe.ofs.backend.services.map.AbstractPageableMapService;
import moe.ofs.backend.util.LuaInteract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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

    private final List<Consumer<LavaEvent>> handlers;

    public SimEventServiceImpl(SimEventRegistryService simEventRegistryService, SimEventPollService simEventPollService) {
        this.simEventRegistryService = simEventRegistryService;
        this.simEventPollService = simEventPollService;

        this.limbo = new ArrayList<>();
        this.handlers = new ArrayList<>();
    }

    @Override
    public void invokeHandlers(LavaEvent lavaEvent) {
        handlers.forEach(handler -> handler.accept(lavaEvent));
    }

    @Override
    public void addHandler(Consumer<LavaEvent> handler) {
        handlers.add(handler);
    }

    @Override
    public void removeHandler(Consumer<LavaEvent> handler) {
        handlers.remove(handler);
    }

    @Scheduled(fixedDelay = 200L)
    @LuaInteract
    public void gather() throws IOException {
        // always try unfinished association first
        limbo.removeIf(event -> event.getAssociateRetryCount() <= retryCountLimit);
        limbo.forEach(simEventRegistryService::associate);

        Map<Boolean, List<SimEvent>> map = simEventPollService.poll().parallelStream()
                .filter(simEvent -> simEvent.getInitiatorId() != 0)
                .map(simEventRegistryService::associate)
                .collect(Collectors.groupingBy(SimEvent::isAssociated));

        if (map.containsKey(true)) {
            map.get(true).stream().map(event -> (LavaEvent) event).forEach(this::invokeHandlers);
        }

        if (map.containsKey(false)) {
            limbo.addAll(map.get(false));
            log.info("{} SimEvents moving to limbo state; total limbo size: {}",
                    map.get(false).size(), limbo.size());
        }
    }


}
