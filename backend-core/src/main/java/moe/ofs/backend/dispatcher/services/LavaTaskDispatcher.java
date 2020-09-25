package moe.ofs.backend.dispatcher.services;

import moe.ofs.backend.dispatcher.model.LavaTask;

import java.util.Map;

/**
 * LavaTaskDispatcher is task dispatcher that can execute a runnable at a fixed rate or only once.
 *
 * This dispatcher maintains a worker queue with a pool of fixed number of threads.
 *
 * It should maintain a list of tasks that can be shutdown when spring context shuts down.
 *
 */
public interface LavaTaskDispatcher {
    void addTask(LavaTask task);

    void removeTask(LavaTask task);

    void removeTaskByName(String name);

    void removeTaskById(long id);

    boolean dispatch(String name);

    Map<String, Boolean> dispatchAll();

    Map<String, Boolean> haltAll();

    void init();
}
