package moe.ofs.backend.function.motd.services;

import moe.ofs.backend.domain.dcs.poll.ExportObject;

public interface MotdTransmitService {

    void transmit(ExportObject exportObject);

    void trigger(Object trigger);
}
