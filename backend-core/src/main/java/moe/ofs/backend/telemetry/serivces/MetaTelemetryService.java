package moe.ofs.backend.telemetry.serivces;

import moe.ofs.backend.dispatcher.model.LavaTask;

import java.util.List;

public interface MetaTelemetryService {
    int getDispatcherTaskCount();

    List<LavaTask> findAllDispatcherTasks();
}
