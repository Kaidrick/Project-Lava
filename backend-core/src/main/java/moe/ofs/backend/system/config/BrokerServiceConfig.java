package moe.ofs.backend.system.config;

import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerServiceConfig {
    @Bean
    public BrokerService broker() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName("embedded-broker-service");
        broker.setPersistent(false);
        broker.addConnector("tcp://localhost:61616");
        broker.addConnector("vm://embedded-broker?broker.persistent=false");
        broker.addConnector("stomp://localhost:61613");
        broker.addConnector("ws://localhost:61618");

        return broker;
    }
}
