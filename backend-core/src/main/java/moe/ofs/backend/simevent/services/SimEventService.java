package moe.ofs.backend.simevent.services;

import moe.ofs.backend.services.CrudService;
import moe.ofs.backend.simevent.model.SimEvent;

public interface SimEventService extends CrudService<SimEvent> {

    void broadcast(SimEvent event);
}
