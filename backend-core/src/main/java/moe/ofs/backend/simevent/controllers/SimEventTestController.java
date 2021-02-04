package moe.ofs.backend.simevent.controllers;

import moe.ofs.backend.simevent.services.SimEventPollService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("sim_event")  // FIXME: fix underscore
public class SimEventTestController {

    private final SimEventPollService simEventPollService;

    public SimEventTestController(SimEventPollService simEventPollService) {
        this.simEventPollService = simEventPollService;
    }

    @GetMapping("test")
    public String test() throws IOException {
        simEventPollService.poll();
        return "ok";
    }
}
