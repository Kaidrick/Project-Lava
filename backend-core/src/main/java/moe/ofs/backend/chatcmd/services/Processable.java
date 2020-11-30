package moe.ofs.backend.chatcmd.services;

import moe.ofs.backend.domain.ChatCommand;

@FunctionalInterface
public interface Processable {
    void process(ChatCommand chatCommand);
}
