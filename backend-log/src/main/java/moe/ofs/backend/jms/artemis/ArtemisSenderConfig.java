package moe.ofs.backend.jms.artemis;

import moe.ofs.backend.jms.Sender;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

//@Configuration
public class ArtemisSenderConfig {

//    @Value("${artemis.broker-url}")
    private String brokerUrl = "tcp://localhost:61616";

    @Bean
    public ActiveMQConnectionFactory artemisSenderActiveMQConnectionFactory() {
        return new ActiveMQConnectionFactory(brokerUrl);
    }

    @Bean
    public CachingConnectionFactory artemisCachingConnectionFactory() {
        return new CachingConnectionFactory(
                artemisSenderActiveMQConnectionFactory());
    }

    @Bean
    public JmsTemplate artemisJmsTemplate() {
        return new JmsTemplate(artemisCachingConnectionFactory());
    }
}