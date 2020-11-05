package moe.ofs.backend.system.controllers;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.dispatcher.services.LavaTaskDispatcher;
import moe.ofs.backend.domain.LogEntry;
import moe.ofs.backend.jms.Sender;
import moe.ofs.backend.object.LogLevel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Controller
@Slf4j
public class FrontendExchangeController {

    private final Sender sender;

    public FrontendExchangeController(Sender sender) {
        this.sender = sender;
    }

    @MessageMapping("/frontend.exchange")  // process request send to /app/front.exchange
    @SendTo("/topic/greetings")            // process and return value to /topic/greetings
    public String greeting(String message) {
//        Thread.sleep(1000);
        LogEntry testEntry = LogEntry.builder()
                .id(1L)
                .logLevel(LogLevel.DEBUG)
                .message(message)
                .source(this.getClass().toString())
                .build();

//        executorService.scheduleWithFixedDelay(() ->
//                sender.sendToTopicAsJson("greetings", testEntry.toString(), "greeting_type"),
//                1000, 1000, TimeUnit.MILLISECONDS);

        return new Gson().toJson(testEntry);
    }
}
