package moe.ofs.backend.jms;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.io.Serializable;

@Slf4j
public class Sender {

    @Autowired
    @Qualifier("jmsTemplate")
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("jmsQueueTemplate")
    private JmsTemplate jmsQueueTemplate;

    public void send(String message) {
        log.info("Sending message: " + message);
        jmsTemplate.convertAndSend("test.topic", message);
    }

    public <T> void sendToTopicAsJson(String topic, T message, String type) {
        jmsTemplate.send(topic, session -> {
            Gson gson = new Gson();
            TextMessage textMessage = session.createTextMessage();
            textMessage.setText(gson.toJson(message));
            textMessage.setStringProperty("type", type);

            return textMessage;
        });
    }

    public <T> void sendToQueueAsJson(String queue, T message, String type) {
        jmsQueueTemplate.send(queue, session -> {
            Gson gson = new Gson();
            TextMessage textMessage = session.createTextMessage();
            textMessage.setText(gson.toJson(message));
            textMessage.setStringProperty("type", type);

//            BytesMessage bytesMessage = new ActiveMQBytesMessage();
//
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            // TODO
//            byte[] bytes = message.toString().getBytes(StandardCharsets.UTF_8);
//            try {
//                byteArrayOutputStream.write(bytes);
////                byteArrayOutputStream.write('\u0000');
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            bytesMessage.writeBytes(byteArrayOutputStream.toByteArray());

            return textMessage;
//            return bytesMessage;
        });
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

