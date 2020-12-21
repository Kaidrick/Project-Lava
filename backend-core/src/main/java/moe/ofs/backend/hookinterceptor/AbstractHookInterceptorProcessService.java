package moe.ofs.backend.hookinterceptor;

import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractHookInterceptorProcessService
        <T extends HookProcessEntity, D extends HookInterceptorDefinition>
        implements HookInterceptorProcessService<T, D> {

    private final Set<HookInterceptorDefinition> definitionSet = new HashSet<>();

    private String name;

    protected String getName() {
        return name;
    }

    @Override
    public void createHook(String name, HookType hookType) {
        this.name = name;
        LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "generic_hook_interceptor/create_hook.lua",
                name, hookType.getFunctionName(), hookType.getPlayerNetIdArgIndex());
    }

    @Override
    public List<HookProcessEntity> poll() throws IOException {
        return LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "generic_hook_interceptor/fetch_decisions.lua",
                getName()).getAsListFor(HookProcessEntity.class);
    }

    @Override
    public List<T> poll(Class<T> tClass) throws IOException {
        return LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "generic_hook_interceptor/fetch_decisions.lua",
                getName()).getAsListFor(tClass);
    }

    @Override
    public boolean addDefinition(D definition) {
        if (definitionSet.add(definition)) {
            String s = LuaScripts.safeLoadAndPrepare(
                    "generic_hook_interceptor/add_definition.lua",
                    name,
                    definition.getName(),
                    definition.getStorage().getRepositoryName(),
                    definition.getPredicateFunction(),
                    definition.getDecisionMappingFunction(),
                    definition.getArgPostProcessFunction()
            );

            return LuaScripts.request(LuaQueryEnv.SERVER_CONTROL, s).getAsBoolean();
        } else {
            return false;
        }
    }

    @Override
    public void removeDefinition(D definition) {
        if (definitionSet.remove(definition)) {
            String s = LuaScripts.loadAndPrepare("generic_hook_interceptor/remove_definition.lua",
                    getClass().getName(),
                    definition.getName()
            );

            LuaScripts.request(LuaQueryEnv.SERVER_CONTROL, s);
        }
    }
}
