package moe.ofs.backend.function.triggermessage.controller;

import moe.ofs.backend.function.triggermessage.model.TriggerMessageRequest;
import moe.ofs.backend.function.triggermessage.services.TriggerMessageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("trigger/message")
public class TriggerMessageController {

    private final TriggerMessageService triggerMessageService;

    public TriggerMessageController(TriggerMessageService triggerMessageService) {
        this.triggerMessageService = triggerMessageService;
    }

    @PostMapping("send")
    public void send(@RequestBody TriggerMessageRequest request) {

    }

    @PostMapping("broadcast")
    public void broadcast() {

    }

}
