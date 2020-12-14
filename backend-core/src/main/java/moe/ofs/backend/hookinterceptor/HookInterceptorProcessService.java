package moe.ofs.backend.hookinterceptor;

import moe.ofs.backend.util.LuaScripts;

import java.io.IOException;
import java.util.List;

/**
 * Used to setup a generic hook interceptor mechanism in server control hook environment.
 *
 * When a hook is run, each function in the predicate will be called.
 * The return value of predicate function will be inserted into a table and fetched to a list.
 *
 * The predicate is defined by a Definition
 * The predicate result is returned as a Decision
 * @param <T>
 */
public interface HookInterceptorProcessService
        <T extends HookProcessEntity, D extends HookInterceptorDefinition> {

    String FUNCTION_RETURN_ORIGINAL_ARGS =
            LuaScripts.load("generic_hook_interceptor/function_template_return_orginal_arguments.lua");

    void createHook(String name, HookType hookType);

    /**
     * Fetch decisions made by the interceptor predicates
     * @return
     * @throws IOException
     */
    List<HookProcessEntity> poll() throws IOException;

    /**
     * Fetch decisions made by interceptor predicates and convert them to List for a specific type
     * @param tClass
     * @return
     * @throws IOException
     */
    List<T> poll(Class<T> tClass) throws IOException;

    /**
     * Add definition of a interceptor to list, then inject predicate function to Lua hook
     * @param definition
     */
    void addDefinition(D definition);

    /**
     * Remove definition of a interceptor from list, then remove predicate function from Lua hook
     * @param definition
     */
    void removeDefinition(D definition);
}
