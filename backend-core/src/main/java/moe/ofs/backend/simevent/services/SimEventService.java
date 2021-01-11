package moe.ofs.backend.simevent.services;

import moe.ofs.backend.domain.events.LavaEvent;
import moe.ofs.backend.common.CrudService;
import moe.ofs.backend.domain.events.SimEvent;

import java.util.function.Consumer;

public interface SimEventService extends CrudService<SimEvent> {

    void invokeHandlers(LavaEvent lavaEvent);

    void addHandler(String name, Consumer<LavaEvent> handler);

    void removeHandler(String name);
}
