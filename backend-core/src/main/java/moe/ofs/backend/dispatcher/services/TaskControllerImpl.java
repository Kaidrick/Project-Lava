package moe.ofs.backend.dispatcher.services;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.dispatcher.model.LavaTask;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TaskControllerImpl implements TaskController {

    private ScheduledExecutorService timer;
    private ExecutorService service;

    private Instant startTime;

    private static final long TIMER_INTERVAL = 100;  // milliseconds

    private static final long TIMER_INIT_DELAY = 0;  // milliseconds

    /**
     * init() method receives directly the repository map from the dispatcher,
     * so that any changes to the map can be reflected in the scheduled allocate method as well.
     *
     * @param taskMap map repository that contains id and task object.
     */
    public void init(Map<Long, LavaTask> taskMap, ExecutorService service) {
        timer = Executors.newSingleThreadScheduledExecutor();
        this.service = service;
        startTime = Instant.now();  // get current time
        timer.scheduleAtFixedRate(() -> this.allocate(taskMap).forEach(task -> {
                    log.info("executing lava task " + task.toString());
                    service.submit(task.getTask());
                }),
                0, 1000, TimeUnit.MILLISECONDS);

        log.info("Task Controller Initialized");
    }

    public void destroy() {
        timer.shutdown();
    }

    public List<LavaTask> allocate(Map<Long, LavaTask> map) {
        return map.values().stream()
                .filter(task -> getTimeElapsed() % task.getInterval() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public long getTimeElapsed() {
        return Instant.now().getEpochSecond() - startTime.getEpochSecond();
    }
}
