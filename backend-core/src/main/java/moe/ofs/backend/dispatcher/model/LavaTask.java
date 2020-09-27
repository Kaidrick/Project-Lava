package moe.ofs.backend.dispatcher.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.ofs.backend.domain.BaseEntity;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * LavaTask class represents a task that can be dispatched by a LavaTaskDispatcher.
 * The dispatcher maintains a list of a map of task, and these tasks can be tracked or
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class LavaTask extends BaseEntity {
    private long interval;
    private AtomicInteger cycles;  // cycles field increments if and only if the runnable is submitted
    private long limit;
    private Instant startTime;
    private Runnable task;

    private Predicate<LavaTask> stopCondition;

    private Runnable callback;

    private long delay;

    private String name;
    private Class<?> source;

    /**
     * LavaTask constructor that creates a task with given name, runnable task, repeat interval, and limit of cyles
     * @param name Name of the task
     * @param task Runnable object
     * @param interval Interval between each execution; if interval is less or equal to zero, it is a run-once task
     * @param limit Number of times of executions before the task is removed from scheduler; if interval is less or
     *              equal to zero, this task is a persistent task and will keep running unless actively removed.
     */
    public LavaTask(String name, Runnable task, long interval, long limit) {
        this.name = name;
        this.task = task;
        this.interval = interval;
        this.limit = limit;

        startTime = Instant.now();
        cycles = new AtomicInteger();
    }

    /**
     * LavaTask constructor that creates a repeatable task with given name, runnable and interval
     * @param name Name of the task
     * @param task Runnable object
     * @param interval Interval between each execution; if interval is less or equal to zero, it is a run-once task
     */
    public LavaTask(String name, Runnable task, long interval) {
        this(name, task, interval, 0);
    }

    /**
     * LavaTask constructor that creates an anonymous task that will only run once, immediately
     * @param task Runnable object
     */
    public LavaTask(Runnable task) {
        this("Anonymous Task - " + UUID.randomUUID(), task, 0);
    }

    // TODO: is this really necessary?
    // TODO: potential problem, if setCallBack() is called twice, how should it behave? replace existing cb?
    public void setCallBack(Runnable callBack) {
        task = () -> {
            task.run();
            callBack.run();
        };
    }
}
