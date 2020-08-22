package moe.ofs.backend.debug.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LuaCommand {
    private String luaString;
    private int level;
    private LocalDateTime timeStamp;
}
