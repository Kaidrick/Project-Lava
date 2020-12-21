package moe.ofs.backend.handlers.starter.services;

import moe.ofs.backend.handlers.starter.model.ScriptInjectionTask;

import java.util.Map;

public interface LuaScriptInjectService {
    boolean add(ScriptInjectionTask task);

    boolean remove(ScriptInjectionTask task);

    Map<ScriptInjectionTask, Boolean> invokeInjection();
}
