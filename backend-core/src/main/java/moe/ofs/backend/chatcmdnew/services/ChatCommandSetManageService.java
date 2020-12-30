package moe.ofs.backend.chatcmdnew.services;

import moe.ofs.backend.chatcmdnew.model.ChatCommandDefinition;

import java.util.Set;
import java.util.function.Predicate;

public interface ChatCommandSetManageService {
    void addCommandDefinition(ChatCommandDefinition definition);

    void updateCommandDefinition(ChatCommandDefinition definition);

    void removeCommandDefinition(ChatCommandDefinition definition);

    Set<ChatCommandDefinition> findAllCommandDefinition();

    Set<ChatCommandDefinition> findCommandDefinition(Predicate<ChatCommandDefinition> predicate);

    ChatCommandDefinition findCommandDefinitionByName(String name);
}
