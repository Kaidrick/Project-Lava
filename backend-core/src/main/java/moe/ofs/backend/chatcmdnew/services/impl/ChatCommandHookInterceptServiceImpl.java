package moe.ofs.backend.chatcmdnew.services.impl;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.chatcmdnew.model.ChatCommandDefinition;
import moe.ofs.backend.chatcmdnew.model.ChatCommandProcessEntity;
import moe.ofs.backend.chatcmdnew.services.ChatCommandHookInterceptService;
import moe.ofs.backend.chatcmdnew.services.ChatCommandSetManageService;
import moe.ofs.backend.function.mizdb.services.impl.LuaStorageInitServiceImpl;
import moe.ofs.backend.handlers.starter.LuaScriptStarter;
import moe.ofs.backend.handlers.starter.model.ScriptInjectionTask;
import moe.ofs.backend.hookinterceptor.AbstractHookInterceptorProcessService;
import moe.ofs.backend.hookinterceptor.HookInterceptorDefinition;
import moe.ofs.backend.hookinterceptor.HookType;
import moe.ofs.backend.services.PlayerDataService;
import moe.ofs.backend.services.mizdb.SimpleKeyValueStorage;
import moe.ofs.backend.util.LuaInteract;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatCommandHookInterceptServiceImpl
        extends AbstractHookInterceptorProcessService<ChatCommandProcessEntity, HookInterceptorDefinition>
        implements ChatCommandHookInterceptService, ChatCommandSetManageService, LuaScriptStarter {

    private final PlayerDataService playerInfoService;
    private final SimpleKeyValueStorage<List<String>> storage;

    private final Set<ChatCommandDefinition> definitionSet;

    public ChatCommandHookInterceptServiceImpl(PlayerDataService playerInfoService) {
        this.playerInfoService = playerInfoService;

        definitionSet = new HashSet<>();

        storage = new SimpleKeyValueStorage<>(
                "lava-chat-command-hook-intercept-service-data-storage",
                LuaQueryEnv.SERVER_CONTROL);
    }

    @Override
    public ScriptInjectionTask injectScript() {
        return ScriptInjectionTask.builder()
                .scriptIdentName("ChatCommandHookInterceptService")
                .initializrClass(getClass())
                .dependencyInitializrClass(LuaStorageInitServiceImpl.class)
                .inject(() -> {
                    boolean hooked = createHook(getClass().getName(), HookType.ON_PLAYER_TRY_SEND_CHAT);

                    HookInterceptorDefinition interceptor = HookInterceptorDefinition.builder()
                            .name("lava-chat-command-interceptor")
                            .predicateFunction("" +
                                    "function(" + HookType.ON_PLAYER_TRY_SEND_CHAT.getFunctionArgsString() + ") " +
                                    "   if storage then " +
                                    "       for kw, sp in pairs(storage:entries()) do " +
                                    "           net.log(kw) " +
                                    "           if msg:sub(1, #kw) == kw then " +
                                    "               return '' " +
                                    "           end " +
                                    "       end " +
                                    "   end " +
                                    "end")
                            .storage(storage)
                            .argPostProcessFunction("" +
                                    "function(" + HookType.ON_PLAYER_TRY_SEND_CHAT.getFunctionArgsString() + ") " +
                                    "   if storage then " +
                                    "       for kw, sp in pairs(storage:entries()) do " +
                                    "           if msg:sub(1, #kw) == kw then " +
                                    "               return { " +
                                    "                   keyword = kw, " +
                                    "                   message = msg " +
                                    "               } " +
                                    "           end " +
                                    "       end " +
                                    "   end " +
                                    "end ")
                            .build();

                    return hooked && addDefinition(interceptor);
                })
                .injectionDoneCallback(aBoolean -> {
                    if (aBoolean) log.info("Hook Interceptor Initialized: {}", getName());
                    else log.error("Failed to initiate Chat Command Hook Intercept Injection Service");
                })
                .build();
    }

    @Scheduled(fixedDelay = 100L)
    @LuaInteract
    public void gather() throws IOException {
        poll(ChatCommandProcessEntity.class).stream()
                .peek(hookProcessEntity ->  // match and set player info if exists
                        playerInfoService.findByNetId(hookProcessEntity.getNetId())
                                .ifPresent(hookProcessEntity::setPlayer))
//                .peek(System.out::println)
                .forEach(e -> definitionSet.stream()
                        .filter(d -> d.getKeyword().equals(e.getKeyword()))  // match unique keyword
                        .forEach(definition -> definition.getConsumer().accept(e)));  // call consumer#accept
    }

    @Override
    public void addCommandDefinition(ChatCommandDefinition definition) {
        definitionSet.add(definition);
        storage.save(definition.getKeyword(), definition.getAffectedPlayerUcidList());
    }

    @Override
    public void updateCommandDefinition(ChatCommandDefinition definition) {
        definitionSet.stream().filter(d -> d.equals(definition)).findAny().ifPresent(d -> {
            d.setConsumer(definition.getConsumer());
            d.setAffectedPlayerUcidList(definition.getAffectedPlayerUcidList());
            d.setStrategy(d.getStrategy());

            storage.save(definition.getKeyword(), definition.getAffectedPlayerUcidList());
        });
    }

    @Override
    public void removeCommandDefinition(ChatCommandDefinition definition) {
        definitionSet.remove(definition);
        storage.delete(definition.getKeyword());
    }

    @Override
    public Set<ChatCommandDefinition> findAllCommandDefinition() {
        return new HashSet<>(definitionSet);
    }

    @Override
    public Set<ChatCommandDefinition> findCommandDefinition(Predicate<ChatCommandDefinition> predicate) {
        return definitionSet.stream().filter(predicate).collect(Collectors.toSet());
    }

    @Override
    public ChatCommandDefinition findCommandDefinitionByName(String name) {
        return definitionSet.stream().filter(s -> s.getName().equals(name)).findAny().orElse(null);
    }
}
