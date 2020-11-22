package moe.ofs.backend.debug.controllers;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.debug.model.LuaCommand;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.export.ExportDataRequest;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/lua")
//@Api(
//        tags = "DCS Lua state debug APIs",
//        value = "Provides APIs to loadstring in different DCS Lua environment")
public class LuaDebugConsoleController {

    private final RequestTransmissionService requestTransmissionService;

    public LuaDebugConsoleController(RequestTransmissionService requestTransmissionService) {
        this.requestTransmissionService = requestTransmissionService;
    }

    /**
     * Basic console debug do string method used to load lua string in DCS lua server.
     * Debug Lua string execution should always return a value.
     * The debug command should only be executed if the connection between backend and DCS lua server is established.
     * FIXME: get() method will keep blocking; it should fail fast if connection cannot be made or timeout
     * @param luaCommand the lua command object that contains the actual string, timestamp, and debug environment.
     * @return the String value that is returned from the dcs Lua server.
     */
    @RequestMapping(value = "/debug", method = RequestMethod.POST)
//    @ApiOperation(value = "Sends Lua string to DCS and return a result if necessary")
    public String sendDebugString(
            @RequestBody
//            @ApiParam(value = "JSON string that contains the string to be loaded in specified Lua state",
//                    examples = @Example({@ExampleProperty(
//                            mediaType = "application/json",
//                            value = "{'luaString': 'return env.mission.theatre, 'level': 0, 'timeStamp': '2020-10-15T13:07:52.110Z'}"
//            )}))
                    LuaCommand luaCommand) {

        log.info(luaCommand.toString());

        switch (luaCommand.getLevel()) {
            case 1:  // api env
                return ((ServerDataRequest) requestTransmissionService.send((
                        new ServerDataRequest(RequestToServer.State.DEBUG,
                                luaCommand.getLuaString())))).get();

            case 2:  // export env
                return ((ExportDataRequest) requestTransmissionService.send((
                        new ExportDataRequest(luaCommand.getLuaString())))).get();

            case 3:  // api env
                return ((ServerDataRequest) requestTransmissionService.send((
                        new ServerDataRequest(RequestToServer.State.MISSION,
                                luaCommand.getLuaString())))).get();


            default:  // default to miz env, as the same as case 0
                return ((ServerDataRequest) requestTransmissionService.send(
                        (new ServerDataRequest(luaCommand.getLuaString())
                        ))).get();
        }
    }

    @RequestMapping(value = "/exec", method = RequestMethod.POST)
    public String sendExecString(@RequestBody LuaCommand luaCommand) {
        return "should return a exec state here?";
    }


}
