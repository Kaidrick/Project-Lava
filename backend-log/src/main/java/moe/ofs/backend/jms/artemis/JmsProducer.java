package moe.ofs.backend.jms.artemis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

//@Service
@Slf4j
public class JmsProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void send(String message) {
        jmsTemplate.convertAndSend(message);
        log.info("Sent message='{}'", message);
    }
}