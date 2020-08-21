package moe.ofs.backend.debug.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LuaCommand {
    private String luaString;
    private String level;
    private LocalDateTime timeStamp;
}
