package moe.ofs.backend.system.controllers;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.jms.LogEntry;
import moe.ofs.backend.jms.Sender;
import moe.ofs.backend.domain.jms.LogLevel;
import moe.ofs.backend.system.FrontendStatusMonitor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.jms.TextMessage;

@Controller
@Slf4j
public class FrontendExchangeController {

    private final Sender sender;

    private final FrontendStatusMonitor monitor;

    private static final String FRONTEND_REGISTER_MESSAGE_ENDPOINT = "/frontend.exchange";
    private static final String FRONTEND_BUS_TOPIC = "/topic/frontend.bus";

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
    @MessageMapping(FRONTEND_REGISTER_MESSAGE_ENDPOINT)  // - receive message from /app/front.exchange
    @SendTo(FRONTEND_BUS_TOPIC)            // - process and return value to /topic/greetings
    public String exchangeRegistry(String message) {
        System.out.println("message = " + message);
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

    // TODO
    @PostMapping("/app/message-exchange/push")
    public void testPushMessageToFrontend(@RequestBody String message) {
        jmsTemplate.send("frontend.bus", session -> {
            TextMessage textMessage = session.createTextMessage();
            textMessage.setText(message);
            return textMessage;
        });
    }

//    @MessageMapping("/frontend.exchange")
//    public void registerExchange(@Payload String message) {
//        log.info(message);
//        jmsTemplate.send("frontend.bus", session -> {
//            LogEntry testEntry = LogEntry.builder()
//                    .id(1L)
//                    .logLevel(LogLevel.DEBUG)
//                    .message(message)
//                    .source(this.getClass().toString())
//                    .build();
//            TextMessage textMessage = session.createTextMessage(new Gson().toJson(testEntry));
//
//            textMessage.setStringProperty("content-category", "confirmation");
//
//            return textMessage;
//        });
//    }
}
