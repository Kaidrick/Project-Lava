package moe.ofs.backend.jms.artemis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

//@Service
@Slf4j
public class JmsConsumer {

    @JmsListener(destination = "${spring.jms.template.default-destination}")
    public void receive(String message) {
        log.info("Received message='{}'", message);
    }
}
