package moe.ofs.backend.simevent.services;

import moe.ofs.backend.domain.events.SimEvent;

public interface SimEventRegistryService {
    SimEvent associate(SimEvent event);
}
