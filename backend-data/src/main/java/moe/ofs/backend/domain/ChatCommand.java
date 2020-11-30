package moe.ofs.backend.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ChatCommand {
    private int netId;  // net of the player who sent this message
    private double time;  // timestamp on message sent
    private String command;  // content of the command content

    private transient PlayerInfo player;
}
