package moe.ofs.backend.chatcmdnew.services.impl;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.chatcmdnew.model.ChatCommandProcessEntity;
import moe.ofs.backend.chatcmdnew.services.ChatCommandHookInterceptService;
import moe.ofs.backend.function.mizdb.PersistentKeyValueInjectionBootstrap;
import moe.ofs.backend.handlers.starter.LuaScriptStarter;
import moe.ofs.backend.handlers.starter.model.ScriptInjectionTask;
import moe.ofs.backend.hookinterceptor.*;
import moe.ofs.backend.services.LuaStorageInitServiceImpl;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.services.mizdb.SimpleKeyValueStorage;
import moe.ofs.backend.util.LuaInteract;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ChatCommandHookInterceptServiceImpl
        extends AbstractHookInterceptorProcessService<ChatCommandProcessEntity, HookInterceptorDefinition>
        implements ChatCommandHookInterceptService, LuaScriptStarter {

    private final PlayerInfoService playerInfoService;
    private final SimpleKeyValueStorage<List<String>> storage;

    public ChatCommandHookInterceptServiceImpl(PlayerInfoService playerInfoService) {
        this.playerInfoService = playerInfoService;

        storage = new SimpleKeyValueStorage<>(
                "lava-chat-command-hook-intercept-service-data-storage",
                LuaQueryEnv.SERVER_CONTROL);

        storage.save("/enter", Arrays.asList("test", "best"));
        storage.save("/whoami", Arrays.asList("123", "456"));
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
                .peek(hookProcessEntity ->
                        playerInfoService.findByNetId(hookProcessEntity.getNetId())
                                .ifPresent(hookProcessEntity::setPlayer))
                .forEach(System.out::println);
    }
}
