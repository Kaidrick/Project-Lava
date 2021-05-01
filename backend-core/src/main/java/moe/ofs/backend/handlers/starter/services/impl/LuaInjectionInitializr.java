package moe.ofs.backend.handlers.starter.services.impl;

import com.google.common.base.Functions;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.connector.handlers.scripts.LuaScriptStarter;
import moe.ofs.backend.domain.connector.handlers.scripts.ScriptInjectionTask;
import moe.ofs.backend.handlers.starter.services.LuaScriptInjectService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Manages the the execution or execution ordering of {@link ScriptInjectionTask} so that Lua script can be loaded
 * in Lua environment in the specified order to avoid missing dependency scripts.
 *
 * The injection process is a blocking operation; it will wait for a boolean value indicating whether the script has
 * been loaded without any error.
 */
@Service
@Slf4j
public class LuaInjectionInitializr implements LuaScriptInjectService {
    private static final Set<ScriptInjectionTask> scriptInjectionTasks = new HashSet<>();
    private final ExecutorService service = Executors.newSingleThreadExecutor();

    private int nextOrderId;

    @Override
    public boolean add(ScriptInjectionTask task) {
        return scriptInjectionTasks.add(task);
    }

    @Override
    public boolean remove(ScriptInjectionTask task) {
        return scriptInjectionTasks.remove(task);
    }

    @Override
    public boolean has(ScriptInjectionTask task) {
       return scriptInjectionTasks.contains(task);
    }

    @Override
    public List<ScriptInjectionTask> list() {
        return new ArrayList<>(scriptInjectionTasks);
    }

    @Override
    public Map<ScriptInjectionTask, Boolean> invokeInjection() {

        parseDependencies();

        Map<ScriptInjectionTask, Boolean> injectionResult = new HashMap<>();

        scriptInjectionTasks.stream().sorted(Comparator.comparing(ScriptInjectionTask::getOrder)).forEach(
                task -> {
                    log.info("************* Injecting {}", task.getScriptIdentName());
                    try {
                        boolean isInjected = service.submit(task.getInject())
                                .get(5000, TimeUnit.MILLISECONDS);

                        if (task.getInjectionDoneCallback() != null) {
                            task.getInjectionDoneCallback().accept(isInjected);
                        }

                        injectionResult.put(task, true);

                        log.info("Injection Process Finished for {}", task.getScriptIdentName());
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        log.error("Failed to inject script dependency: {} from {}",
                                task.getScriptIdentName(), task.getDependencyInitializrClass());
                        e.printStackTrace();
                    }
                    injectionResult.put(task, false);
                }
        );

        return injectionResult;
    }

    private void recursivelySetLoadOrder(List<ScriptInjectionTask> list) {
        list.forEach(t -> {
            t.setOrder(nextOrderId++);

            if (t.getDependents() != null && !t.getDependents().isEmpty()) {
                recursivelySetLoadOrder(t.getDependents());
            }
        });
    }

    private void parseDependencies() {
        Map<Class<? extends LuaScriptStarter>, ScriptInjectionTask> starterMap = scriptInjectionTasks.stream()
                .collect(Collectors.toMap(ScriptInjectionTask::getInitializrClass, Functions.identity()));

        Map<String, ScriptInjectionTask> identMap = scriptInjectionTasks.stream()
                .collect(Collectors.toMap(ScriptInjectionTask::getScriptIdentName, Functions.identity()));


        scriptInjectionTasks.forEach(task -> {
            // if a task's dependency name is not null, and dependency is null, use name, otherwise use class
            Class<? extends LuaScriptStarter> dependency = task.getDependencyInitializrClass();
            String dependencyIdentName = task.getDependencyInitializrIdentName();

            if (dependency != null){

                appendDependency(starterMap.get(task.getDependencyInitializrClass()), task);

            } else if (dependencyIdentName != null && identMap.containsKey(dependencyIdentName)) {

                appendDependency(identMap.get(dependencyIdentName), task);

            }
        });

        List<ScriptInjectionTask> tasks = scriptInjectionTasks.stream()  // task without dependency initializr is top level task
                        .filter(task -> task.getDependencyInitializrClass() == null)
                        .collect(Collectors.toList());

        recursivelySetLoadOrder(tasks);

        log.info("Mapping Lua Script Injection Task Dependencies");
        scriptInjectionTasks.stream().sorted(Comparator.comparing(ScriptInjectionTask::getOrder))
                .forEach(task -> log.info("#{} {}, Initializr Class: {}, Dependency: {}",
                        task.getOrder(),
                        task.getScriptIdentName(),
                        task.getInitializrClass().getName(),
                        task.getDependencyInitializrClass() != null ?
                                task.getDependencyInitializrClass().getName() : "None"));
    }

    private void appendDependency(ScriptInjectionTask task, ScriptInjectionTask dependent) {
        if (task.getDependents() == null) {
            task.setDependents(new ArrayList<>());
        }
        task.getDependents().add(dependent);
    }
}
