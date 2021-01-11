package moe.ofs.backend.handlers.starter.services;

import moe.ofs.backend.domain.connector.handlers.scripts.ScriptInjectionTask;

import java.util.Map;

public interface LuaScriptInjectService {
    boolean add(ScriptInjectionTask task);

    boolean remove(ScriptInjectionTask task);

    Map<ScriptInjectionTask, Boolean> invokeInjection();
}
