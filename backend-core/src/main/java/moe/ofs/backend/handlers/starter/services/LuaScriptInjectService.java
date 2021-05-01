package moe.ofs.backend.handlers.starter.services;

import moe.ofs.backend.domain.connector.handlers.scripts.ScriptInjectionTask;

import java.util.List;
import java.util.Map;

public interface LuaScriptInjectService {
    boolean add(ScriptInjectionTask task);

    boolean remove(ScriptInjectionTask task);

    boolean has(ScriptInjectionTask task);

    List<ScriptInjectionTask> list();

    Map<ScriptInjectionTask, Boolean> invokeInjection();
}
