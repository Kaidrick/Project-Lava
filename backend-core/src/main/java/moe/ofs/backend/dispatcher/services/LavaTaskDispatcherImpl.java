package moe.ofs.backend.dispatcher.services;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.dispatcher.model.LavaTask;
import moe.ofs.backend.services.map.AbstractMapService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A cached thread pool is maintained to receive work from a controller.
 * The controller knows all appended task, and if the task is repeatable, it will be put into a list.
 * The controller consists of a timer and a priority queue.
 * If the user specifies that priority should be respected when tasks are to be executed,
 * the controller will send tasks with higher priority to the work queue first.
 *
 *
 *
 */
@Slf4j
@Service
public class LavaTaskDispatcherImpl extends AbstractMapService<LavaTask> implements LavaTaskDispatcher {

    private ExecutorService service;

    private ScheduledExecutorService timer;

    private TaskController taskController;


    public LavaTaskDispatcherImpl(TaskController taskController) {
        this.taskController = taskController;
    }

    @Override
    public void init() {
        service = Executors.newCachedThreadPool();
        timer = Executors.newSingleThreadScheduledExecutor();

        taskController.init(map, service);

        log.info("Lava Task Dispatcher Initialized.");
    }

    /**
     *
     *
     *
     * @param task The task to be executed in a specified manner.
     */
    @Override
    public void addTask(LavaTask task) {
        save(task);
        log.info("added task " + task.toString());
    }

    @Override
    public void removeTask(LavaTask task) {

    }

    /**
     * If the task is still running, wait for the task to complete and then iterate through the list to
     * find the object and then remove it from the list.
     * @param name Name of the task
     */
    @Override
    public void removeTaskByName(String name) {
        map.entrySet().removeIf(entry -> entry.getValue().getName().equals(name));
    }

    @Override
    public void removeTaskById(long id) {
        map.remove(id);
    }

    @Override
    public boolean dispatch(String name) {
        return false;
    }

    @Override
    public Map<String, Boolean> dispatchAll() {
        return null;
    }

    @Override
    public Map<String, Boolean> haltAll() {
        timer.shutdown();
        try {
            timer.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            timer.shutdownNow();
        }

        service.shutdown();
        try {
            service.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            service.shutdownNow();
        }

        taskController.shutdown();

        map.clear();

        log.info("Dispatcher halted");

        return null;
    }
}
