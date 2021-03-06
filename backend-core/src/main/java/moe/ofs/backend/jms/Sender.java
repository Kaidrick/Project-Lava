package moe.ofs.backend.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ObjectMessage;
import java.io.Serializable;

@Slf4j
public class Sender {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void send(String message) {
        log.info("Sending message: " + message);
        jmsTemplate.convertAndSend("test.topic", message);
    }

    public <T extends Serializable> void sendToTopic(String topic, T message, String type) {

//        log.info("Sending message to topic:" + topic + " -> " + message.toString());
//        jmsTemplate.convertAndSend(topic, message);

        jmsTemplate.send(topic, session -> {
            ObjectMessage objectMessage = session.createObjectMessage();
            objectMessage.setObject(message);
            objectMessage.setStringProperty("type", type);

            return objectMessage;
        });
    }
}

