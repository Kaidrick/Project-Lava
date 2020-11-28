package moe.ofs.backend.simevent.services;

import moe.ofs.backend.domain.LavaEvent;
import moe.ofs.backend.services.CrudService;
import moe.ofs.backend.domain.SimEvent;

public interface SimEventService extends CrudService<SimEvent> {

    void broadcast(LavaEvent event);
}
