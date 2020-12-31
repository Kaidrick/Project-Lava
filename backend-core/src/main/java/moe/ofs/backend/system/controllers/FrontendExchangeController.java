package moe.ofs.backend.system.controllers;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.LogEntry;
import moe.ofs.backend.jms.Sender;
import moe.ofs.backend.object.LogLevel;
import moe.ofs.backend.system.FrontendStatusMonitor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import javax.jms.TextMessage;

@Controller
@Slf4j
public class FrontendExchangeController {

    private final Sender sender;

    private final FrontendStatusMonitor monitor;

    private final static String FRONTEND_REGISTER_MESSAGE_ENDPOINT = "/frontend.exchange";
    private final static String FRONTEND_BUS_TOPIC = "/topic/frontend.bus";

    private final JmsTemplate jmsTemplate;

    public FrontendExchangeController(Sender sender, FrontendStatusMonitor monitor, JmsTemplate jmsTemplate) {
        this.sender = sender;
        this.monitor = monitor;
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * - @MessageMapping: Performs mapping of Spring Message with message handling methods.
     * - @SendTo: Converts method return value to Spring Message and send it to specified destination.
     */
//    @MessageMapping("/frontend.exchange")  // - receive message from /app/front.exchange
//    @SendTo("/topic/frontend.bus")            // - process and return value to /topic/greetings
    public String exchangeRegistry(String message) {
        monitor.changeStatus(FrontendStatusMonitor.Status.CONNECTED);
//        Thread.sleep(1000);
        LogEntry testEntry = LogEntry.builder()
                .id(1L)
                .logLevel(LogLevel.DEBUG)
                .message(message)
                .source(this.getClass().toString())
                .build();
        return new Gson().toJson(testEntry);
    }

    @MessageMapping("/frontend.exchange")
    public void registerExchange(@Payload String message) {
        log.info(message);
        jmsTemplate.send("frontend.bus", session -> {
            LogEntry testEntry = LogEntry.builder()
                    .id(1L)
                    .logLevel(LogLevel.DEBUG)
                    .message(message)
                    .source(this.getClass().toString())
                    .build();
            TextMessage textMessage = session.createTextMessage(new Gson().toJson(testEntry));

            textMessage.setStringProperty("content-category", "confirmation");

            return textMessage;
        });
    }
}
