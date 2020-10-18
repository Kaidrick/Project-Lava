package moe.ofs.backend.config.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("server")
public class DcsServerController {

    // restart
    @RequestMapping(value = "/control")
    public @ResponseBody String setServerControl(String string) {
        return "test ok";
    }

    // shutdown

    // background task service halt

    // lava system shutdown: shutdown background tasks and task dispatcher, then shutdown spring boot starter actuator

}
