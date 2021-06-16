package moe.ofs.backend.system.controllers;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.jms.LogEntry;
import moe.ofs.backend.domain.jms.LogLevel;
import moe.ofs.backend.jms.Sender;
import moe.ofs.backend.security.annotation.CheckPermission;
import moe.ofs.backend.system.FrontendStatusMonitor;
import moe.ofs.backend.system.model.WebSocketAuthInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.jms.TextMessage;
import java.util.UUID;

@RestController
@Slf4j
public class FrontendExchangeController {

    private final Sender sender;

    private final Cache websocketAuthCache;

    private final FrontendStatusMonitor monitor;

    private static final String FRONTEND_REGISTER_MESSAGE_ENDPOINT = "/frontend.exchange";
    private static final String FRONTEND_BUS_TOPIC = "/topic/frontend.bus";

    private final JmsTemplate jmsTemplate;


    public FrontendExchangeController(Sender sender, FrontendStatusMonitor monitor,
                                      JmsTemplate jmsTemplate,
                                      @Qualifier("websocketAuthCacheManager") CacheManager cacheManager) {
        this.sender = sender;
        this.monitor = monitor;
        this.jmsTemplate = jmsTemplate;

        this.websocketAuthCache = cacheManager.getCache("websockets");
    }

    @CheckPermission(requiredAccessToken = true)
    @GetMapping("/app/ws-token")
    public String requestWebsocketHandshakeToken(@RequestHeader("access_token") String accessToken) {
        UUID websocketAuthToken = UUID.randomUUID();

        WebSocketAuthInfo authInfo = new WebSocketAuthInfo();
        authInfo.setOneTimeToken(websocketAuthToken.toString());
        authInfo.setTimestamp(System.currentTimeMillis());
        authInfo.setAccessToken(accessToken);

        websocketAuthCache.put(websocketAuthToken.toString(), authInfo);

        return websocketAuthToken.toString();
    }

    /**
     * - @MessageMapping: Performs mapping of Spring Message with message handling methods.
     * - @SendTo: Converts method return value to Spring Message and send it to specified destination.
     */
    @MessageMapping("/frontend.exchange/{userIdent}")  // - receive message from /app/front.exchange
    @SendTo("/topic/frontend.bus")            // - process and return value to /topic/greetings
    public String exchangeRegistry(String message, SimpMessageHeaderAccessor accessor, @DestinationVariable String userIdent) {
        System.out.println("userIdent = " + userIdent);
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
