package moe.ofs.backend.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;

@Slf4j
public class Receiver {
    @JmsListener(destination = "test.topic")
    public void receive(String message) {
        log.info("Message received: " + message);
    }
}
