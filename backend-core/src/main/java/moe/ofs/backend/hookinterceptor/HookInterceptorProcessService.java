package moe.ofs.backend.hookinterceptor;

import moe.ofs.backend.dataservice.aware.PlayerInfoServiceAware;
import moe.ofs.backend.connector.util.LuaScripts;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

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
        <T extends HookProcessEntity, D extends HookInterceptorDefinition> extends PlayerInfoServiceAware {

    String FUNCTION_RETURN_ORIGINAL_ARGS =
            LuaScripts.load("generic_hook_interceptor/function_template_return_orginal_arguments.lua");

    String FUNCTION_EMPTY_BLOCK = "function(...) end";

    boolean createHook(String name, HookType hookType);

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
     * @return
     */
    boolean addDefinition(D definition);

    /**
     * Remove definition of a interceptor from list, then remove predicate function from Lua hook
     * @param definition
     */
    void removeDefinition(D definition);


    boolean addProcessor(HookRecordProcessor<T> processor);

    boolean addProcessor(String name, Consumer<T> action);

    void removeProcessor(HookRecordProcessor<T> processor);

    void removeProcessor(String processorName);

//    void gather() throws IOException;

    void gather(Class<T> tClass) throws IOException;
}
