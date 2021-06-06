package moe.ofs.backend.jms;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.io.Serializable;
import java.util.Map;

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

    public void sendQueueTextMessage(TextMessage textMessage) throws JMSException {
        jmsTemplate.send(textMessage.getJMSDestination(), session -> textMessage);
    }

    public void sendTopicTextMessage(TextMessage textMessage) throws JMSException {
        jmsQueueTemplate.send(textMessage.getJMSDestination(), session -> textMessage);
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

    public void sendToQueue(String queue, String message, Map<String, String> headers) {
        jmsQueueTemplate.send(queue, session -> {
            TextMessage textMessage = session.createTextMessage();
            textMessage.setText(message);

            if (null != headers) {
                headers.forEach((key, value) -> {
                    try {
                        textMessage.setStringProperty(key, value);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                });
            }
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

