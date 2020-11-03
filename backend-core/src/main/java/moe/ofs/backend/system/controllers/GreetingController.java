package moe.ofs.backend.system.controllers;

import com.google.gson.Gson;
import moe.ofs.backend.domain.LogEntry;
import moe.ofs.backend.object.LogLevel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) throws Exception {
        Thread.sleep(1000);
        LogEntry testEntry = LogEntry.builder()
                .id(1L)
                .logLevel(LogLevel.DEBUG)
                .message("test message")
                .source(this.getClass().toString())
                .build();

        return new Gson().toJson(testEntry);
    }
}
