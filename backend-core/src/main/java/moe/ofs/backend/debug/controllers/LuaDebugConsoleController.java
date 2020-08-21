package moe.ofs.backend.debug.controllers;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.debug.model.LuaCommand;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerExecRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/lua")
public class LuaDebugConsoleController {
    @RequestMapping(value = "/debug", method = RequestMethod.POST)
    public String sendDebugString(@RequestBody LuaCommand luaCommand) {

        log.info(luaCommand.toString());

        ServerExecRequest serverExecRequest =
                new ServerExecRequest(RequestToServer.State.DEBUG, luaCommand.getLuaString());
        serverExecRequest.send();
        return "good";
    }
}
