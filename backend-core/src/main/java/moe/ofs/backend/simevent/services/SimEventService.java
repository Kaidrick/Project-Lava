package moe.ofs.backend.simevent.services;

import moe.ofs.backend.domain.LavaEvent;
import moe.ofs.backend.common.CrudService;
import moe.ofs.backend.domain.SimEvent;

import java.util.function.Consumer;

public interface SimEventService extends CrudService<SimEvent> {

    void invokeHandlers(LavaEvent lavaEvent);

    void addHandler(Consumer<LavaEvent> handler);

    void removeHandler(Consumer<LavaEvent> handler);
}
