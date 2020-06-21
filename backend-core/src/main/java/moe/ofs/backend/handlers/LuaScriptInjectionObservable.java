package moe.ofs.backend.handlers;

import java.util.ArrayList;
import java.util.List;

/**
 * Should happens before MissionStartObservable
 */
public interface LuaScriptInjectionObservable {
    List<LuaScriptInjectionObservable> list = new ArrayList<>();

    void observe();

    default void register() {
        list.add(this);
    }
    default void unregister() {
        list.remove(this);
    }

    static void invokeAll() {
        new ArrayList<>(list).forEach(LuaScriptInjectionObservable::observe);
    }
}
