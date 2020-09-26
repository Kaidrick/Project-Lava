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

    private static final long TIMER_INIT_DELAY = 10;  // milliseconds

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
        timer.scheduleAtFixedRate(() -> {
            // remove task from map if stop condition is met
            taskMap.entrySet().stream()

                    .filter(entry -> entry.getValue().getStopCondition() != null && entry.getValue().getStopCondition()
                            .test(entry.getValue()))  // filter task entry whose task meets the stop condition

                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList())
                    .forEach(taskMap::remove);  // remove task from task map by key (Long id)

            this.allocate(taskMap).forEach(this::runTask);
        }, TIMER_INIT_DELAY, TIMER_INTERVAL, TimeUnit.MILLISECONDS);

        log.info("Task Controller Initialized");
    }

    @Override
    public void shutdown() {
        timer.shutdown();
        try {
            timer.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            timer.shutdownNow();
        }
    }

    public void runTask(LavaTask task) {
        service.submit(task.getTask());
        task.getCycles().incrementAndGet();
        log.info("Dispatched " + task);
    }

    public List<LavaTask> allocate(Map<Long, LavaTask> map) {
//        System.out.println("allocate");
        return map.values().stream().filter(this::canRun).collect(Collectors.toList());
    }

    /**
     * Calculate the timestamp at which the given task should be submitted to the execute service
     * @param task Runnable object
     * @return long value indicating the next submit time in epoch milliseconds
     */
    public long nextRunTimeStamp(LavaTask task) {
        try {
            return task.getStartTime().toEpochMilli() + task.getCycles().get() * task.getInterval();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean canRun(LavaTask task) {
        return Instant.now().toEpochMilli() - nextRunTimeStamp(task) >= 0;
    }

    @Override
    public long getTimeElapsed() {
//        System.out.println(Instant.now().toEpochMilli() - startTime.toEpochMilli());
        return Instant.now().toEpochMilli() - startTime.toEpochMilli();
    }
}
