package moe.ofs.backend.handlers.starter.model;

import lombok.Builder;
import lombok.Data;
import moe.ofs.backend.handlers.starter.LuaScriptStarter;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

@Data
public class ScriptInjectionTask {
    private String scriptIdentName;  // unique ident name
    private Class<? extends LuaScriptStarter> dependencyInitializrClass;  // the class whose script should be injected before that of this class
    private String dependencyInitializrIdentName;  //
    private Class<? extends LuaScriptStarter> initializrClass;  // this initializr class
    private String luaPredicateFunction;  // busy waiting predicate function to be used to check if inject can be done
    private Callable<Boolean> inject;  // the injection, should return a boolean value indicating successful injection
    private Consumer<Boolean> injectionDoneCallback;  // called when inject process complete, successful or failed

    private int order;  // the order decided by LuaInjectionInitializr based on class initializr dependency
    private boolean injected;  // injected flag
    private List<ScriptInjectionTask> dependents;

    @Builder
    public ScriptInjectionTask(String scriptIdentName,
                               Class<? extends LuaScriptStarter> initializrClass,

                               String dependencyInitializrIdentName,
                               Class<? extends LuaScriptStarter> dependencyInitializrClass,

                               String luaPredicateFunction,
                               Callable<Boolean> inject, Consumer<Boolean> injectionDoneCallback) {

        this.scriptIdentName = scriptIdentName;
        this.dependencyInitializrClass = dependencyInitializrClass;
        this.dependencyInitializrIdentName = dependencyInitializrIdentName;
        this.initializrClass = initializrClass;
        this.luaPredicateFunction = luaPredicateFunction;
        this.inject = inject;
        this.injectionDoneCallback = injectionDoneCallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScriptInjectionTask that = (ScriptInjectionTask) o;
        return Objects.equals(scriptIdentName, that.scriptIdentName) && Objects.equals(initializrClass, that.initializrClass) && Objects.equals(inject, that.inject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scriptIdentName, initializrClass, inject);
    }
}
