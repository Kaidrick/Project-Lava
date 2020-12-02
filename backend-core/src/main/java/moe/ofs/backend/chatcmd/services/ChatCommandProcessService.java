package moe.ofs.backend.chatcmd.services;

import moe.ofs.backend.chatcmd.model.ChatCommandDefinition;
import moe.ofs.backend.domain.ChatCommand;

import java.io.IOException;
import java.util.List;

public interface ChatCommandProcessService {
    List<ChatCommand> poll() throws IOException;

    void analysis(ChatCommand command);

    void addProcessor(Processable processor);

    void addProcessors(List<Processable> processors);

    void removeProcessor(Processable processor);

    void removeProcessors(List<Processable> processors);

    ChatCommandDefinition addDefinition(ChatCommandDefinition definition);
}
