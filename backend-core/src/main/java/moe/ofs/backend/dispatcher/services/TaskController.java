package moe.ofs.backend.dispatcher.services;

import moe.ofs.backend.dispatcher.model.LavaTask;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public interface TaskController {
    long getTimeElapsed();

    List<LavaTask> allocate(Map<Long, LavaTask> map);

    void init(Map<Long, LavaTask> map, ExecutorService service);

    void shutdown();
}
