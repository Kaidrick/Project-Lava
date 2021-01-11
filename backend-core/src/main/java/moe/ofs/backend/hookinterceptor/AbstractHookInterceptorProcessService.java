package moe.ofs.backend.hookinterceptor;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.connector.lua.LuaInteract;
import moe.ofs.backend.dataservice.player.PlayerInfoService;
import moe.ofs.backend.connector.util.LuaScripts;
import moe.ofs.backend.connector.lua.LuaQueryEnv;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
public abstract class AbstractHookInterceptorProcessService
        <T extends HookProcessEntity, D extends HookInterceptorDefinition>
        implements HookInterceptorProcessService<T, D> {

    protected PlayerInfoService playerInfoService;

    private final Set<HookInterceptorDefinition> definitionSet = new HashSet<>();

    private final Set<HookRecordProcessor<T>> processorSet = new HashSet<>();

    private String name;

    protected String getName() {
        return name;
    }

    @Override
    public boolean createHook(String name, HookType hookType) {
        this.name = name;
        return LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL,
                "generic_hook_interceptor/create_hook.lua",
                name, hookType.getFunctionName(), hookType.getPlayerNetIdArgIndex(),
                hookType.isInterceptAllowed()).getAsBoolean();
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

    @Override
    public boolean addProcessor(HookRecordProcessor<T> processor) {
        return processorSet.add(processor);
    }

    @Override
    public boolean addProcessor(String name, Consumer<T> action) {
        return addProcessor(new HookRecordProcessor<>(name, action));
    }

    @Override
    public void removeProcessor(HookRecordProcessor<T> processor) {
        processorSet.remove(processor);
    }

    @Override
    public void removeProcessor(String processorName) {
        processorSet.removeIf(processor -> processor.getName().equals(processorName));
    }

    @LuaInteract
    @Override
    public void gather(Class<T> tClass) throws IOException {
        poll(tClass).stream()
                .peek(hookProcessEntity ->  // match and set player info if exists
                        playerInfoService.findByNetId(hookProcessEntity.getNetId())
                                .ifPresent(hookProcessEntity::setPlayer))
                .forEach(this::processEntity);
    }

    private void processEntity(T t) {
        try {
            processorSet.forEach(p -> p.getAction().accept(t));  // call consumer#accept
        } catch (Exception exception) {
            log.error("Failed to process hook record due to: ", exception);
        }
    }

    @Override
    public void setPlayerInfoService(PlayerInfoService playerInfoService) {
        this.playerInfoService = playerInfoService;
    }
}
