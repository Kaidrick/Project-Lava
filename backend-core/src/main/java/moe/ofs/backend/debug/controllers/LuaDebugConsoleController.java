package moe.ofs.backend.debug.controllers;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.debug.model.LuaCommand;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.export.ExportDataRequest;
import moe.ofs.backend.request.server.ServerDataRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/lua")
public class LuaDebugConsoleController {

    /**
     * Basic console debug do string method used to load lua string in DCS lua server.
     * Debug Lua string execution should always return a value.
     * @param luaCommand the lua command object that contains the actual string, timestamp, and debug environment.
     * @return the String value that is returned from the dcs Lua server.
     */
    @RequestMapping(value = "/debug", method = RequestMethod.POST)
    public String sendDebugString(@RequestBody LuaCommand luaCommand) {

        log.info(luaCommand.toString());

        switch (luaCommand.getLevel()) {
            case 1:  // api env
                return ((ServerDataRequest) new ServerDataRequest(RequestToServer.State.DEBUG,
                        luaCommand.getLuaString()).send()).get();

            case 2:  // export env
                return ((ExportDataRequest) new ExportDataRequest(luaCommand.getLuaString()).send()).get();


            default:  // default to miz env, as the same as case 0
                return ((ServerDataRequest) new ServerDataRequest(luaCommand.getLuaString()).send()).get();
        }
    }

    @RequestMapping(value = "/exec", method = RequestMethod.POST)
    public String sendExecString(@RequestBody LuaCommand luaCommand) {
        return "should return a exec state here?";
    }


}
