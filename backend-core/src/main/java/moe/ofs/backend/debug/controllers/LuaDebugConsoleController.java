package moe.ofs.backend.debug.controllers;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.connector.lua.LuaQueryEnv;
import moe.ofs.backend.connector.util.LuaScripts;
import moe.ofs.backend.debug.model.LuaCommand;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/lua")
@Api(
        tags = "DCS Lua state debug APIs",
        value = "Provides APIs to loadstring in different DCS Lua environment")
@ApiSupport(author = "北欧式的简单")
public class LuaDebugConsoleController {
    /**
     * Basic console debug do string method used to load lua string in DCS lua server.
     * Debug Lua string execution should always return a value.
     * The debug command should only be executed if the connection between backend and DCS lua server is established.
     *
     * @param luaCommand the lua command object that contains the actual string, timestamp, and debug environment.
     * @return the String value that is returned from the dcs Lua server.
     */
    @RequestMapping(value = "/debug", method = RequestMethod.POST)
    @ApiOperation(value = "Lua调试接口")
    public String sendDebugString(
            @ApiParam
            @RequestBody LuaCommand luaCommand) {

        log.info(luaCommand.toString());

        switch (luaCommand.getLevel()) {
            case 1:  // api env
                return LuaScripts.request(LuaQueryEnv.SERVER_CONTROL, luaCommand.getLuaString()).get();

            case 2:  // export env
                return LuaScripts.request(LuaQueryEnv.EXPORT, luaCommand.getLuaString()).get();

            case 3:  // api env
                return LuaScripts.request(LuaQueryEnv.TRIGGER, luaCommand.getLuaString()).get();

            default:  // default to miz env, as the same as case 0
                return LuaScripts.request(LuaQueryEnv.MISSION_SCRIPTING, luaCommand.getLuaString()).get();
        }
    }

    @RequestMapping(value = "/exec", method = RequestMethod.POST)
    public String sendExecString(
            @ApiParam
            @RequestBody LuaCommand luaCommand
    ) {
        return "should return a exec state here?";
    }


}
