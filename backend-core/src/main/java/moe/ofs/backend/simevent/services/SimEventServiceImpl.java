package moe.ofs.backend.simevent.services;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.events.LavaEvent;
import moe.ofs.backend.domain.events.SimEvent;
import moe.ofs.backend.common.AbstractPageableMapService;
import moe.ofs.backend.connector.lua.LuaInteract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    private final Map<String, Consumer<LavaEvent>> handlers;

    public SimEventServiceImpl(SimEventRegistryService simEventRegistryService, SimEventPollService simEventPollService) {
        this.simEventRegistryService = simEventRegistryService;
        this.simEventPollService = simEventPollService;

        this.limbo = new ArrayList<>();
        this.handlers = new HashMap<>();
    }

    @Override
    public void invokeHandlers(LavaEvent lavaEvent) {
        System.out.println("published lavaEvent = " + lavaEvent);
        handlers.values().forEach(handler -> handler.accept(lavaEvent));
    }

    @Override
    public void addHandler(String name, Consumer<LavaEvent> handler) {
        handlers.put(name, handler);
    }

    @Override
    public void removeHandler(String name) {
        handlers.remove(name);
    }

    @Scheduled(fixedDelay = 200L)
    @LuaInteract
    public void gather() throws IOException {
        // always try unfinished association first
        limbo.forEach(simEventRegistryService::associate);

        // find already associated and events that has already reached the retry count limit
        List<SimEvent> processed = limbo.stream()
                .filter(s -> s.isAssociated() || s.getAssociateRetryCount() >= retryCountLimit)
                .collect(Collectors.toList());

        limbo.removeAll(processed);

        // publish final lava event
        processed.stream()
                .peek(s -> s.setAssociated(true))
                .map(event -> (LavaEvent) event)
                .forEach(this::invokeHandlers);


        Map<Boolean, List<SimEvent>> map = simEventPollService.poll().parallelStream()
                .filter(simEvent -> simEvent.getInitiatorId() != 0)
                .map(simEventRegistryService::associate)
                .collect(Collectors.partitioningBy(SimEvent::isAssociated));

        map.get(true).stream().map(event -> (LavaEvent) event)
                .forEach(this::invokeHandlers);

        if (map.get(false).size() > 0) {
            limbo.addAll(map.get(false));
            log.info("{} SimEvents moving to limbo state; total limbo size: {}",
                    map.get(false).size(), limbo.size());
        }
    }


}
