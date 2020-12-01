package moe.ofs.backend.chatcmd.model;

import lombok.Getter;
import lombok.Setter;
import moe.ofs.backend.domain.ChatCommand;

import java.util.function.Consumer;

@Getter
@Setter
public class ChatCommandDefinition {
    private String name;
    private String keyword;
    private Consumer<ChatCommand> consumer;
}
