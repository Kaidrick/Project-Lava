package moe.ofs.backend.chatcmd.services;

import moe.ofs.backend.domain.chatcmd.ChatCommand;

@FunctionalInterface
public interface Processable {
    void process(ChatCommand chatCommand);
}
