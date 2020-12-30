package moe.ofs.backend.chatcmdnew.services;

import moe.ofs.backend.chatcmdnew.model.ChatCommandDefinition;
import moe.ofs.backend.chatcmdnew.model.ChatCommandProcessEntity;
import moe.ofs.backend.hookinterceptor.HookInterceptorDefinition;
import moe.ofs.backend.hookinterceptor.HookInterceptorProcessService;

public interface ChatCommandHookInterceptService
        extends HookInterceptorProcessService<ChatCommandProcessEntity, HookInterceptorDefinition> {
    void addCommandDefinition(ChatCommandDefinition definition);

    void updateCommandDefinition(ChatCommandDefinition definition);

    void removeCommandDefinition(ChatCommandDefinition definition);
}
