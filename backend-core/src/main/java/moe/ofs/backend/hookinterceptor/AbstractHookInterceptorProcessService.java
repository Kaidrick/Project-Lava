package moe.ofs.backend.hookinterceptor;

import com.google.gson.internal.LinkedTreeMap;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        String s = LuaScripts.loadAndPrepare(
                "generic_hook_interceptor/create_hook.lua",
                name, hookType.getFunctionName(), hookType.getPlayerNetIdArgIndex());

        LuaScripts.request(LuaQueryEnv.SERVER_CONTROL, s);
    }

    @Override
    public List<HookProcessEntity> poll() throws IOException {
        return LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "generic_hook_interceptor/fetch_decisions.lua",
                getName()).getAsListFor(LinkedTreeMap.class).stream()
                .map(linkedTreeMap -> {
                    HookProcessEntity entity = new HookProcessEntity();
                    if (linkedTreeMap.containsKey("__entity_player_id")) {
                        entity.setNetId(Double.valueOf((double) linkedTreeMap.get("__entity_player_id")).intValue());
                        linkedTreeMap.remove("__entity_player_id");
                    }
                    if (linkedTreeMap.containsKey("__entity_target")) {
                        entity.setHookType(
                                HookType.ofFunctionName(
                                        String.valueOf(linkedTreeMap.get("__entity_target")))
                        );
                        linkedTreeMap.remove("__entity_target");
                    }
                    if (linkedTreeMap.containsKey("__entity_definition_name")) {
                        entity.setDefinitionName(String.valueOf(linkedTreeMap.get("__entity_definition_name")));
                    }
                    if (linkedTreeMap.containsKey("__predicate_result")) {
                        entity.setMeta(linkedTreeMap.get("__predicate_result"));
                    }

                    return entity;
                }).collect(Collectors.toList());
    }

    @Override
    public void addDefinition(D definition) {
        if (definitionSet.add(definition)) {
            String s = LuaScripts.safeLoadAndPrepare(
                    "generic_hook_interceptor/add_definition.lua",
                    name,
                    definition.getName(),
                    definition.getStorage().getRepositoryName(),
                    definition.getPredicateFunction(),
                    definition.getDecisionMappingFunction()
            );

            LuaScripts.request(LuaQueryEnv.SERVER_CONTROL, s);
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
