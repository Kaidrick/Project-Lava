package moe.ofs.backend.debug.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "LuaCommand", description = "JSON string that contains the string to be loaded in specified Lua state")
public class LuaCommand {
    @ApiModelProperty(value = "lua代码", example = "return env.mission.theatre")
    private String luaString;
    @ApiModelProperty(value = "级别", example = "0")
    private int level;
    @ApiModelProperty(value = "时间戳", example = "2020-10-15T13:07:52.110Z")
    private LocalDateTime timeStamp;
}
