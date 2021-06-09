package moe.ofs.backend.system.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

//@Configuration
@PropertySource(value = "classpath:activemq-ssl-config.properties", encoding = "UTF-8")
@ConfigurationProperties(prefix = "activemq.ssl")
@Getter
@Setter
public class ActiveMQProperties {
    private String brokerUrl = "ssl://localhost:61613";
    private String brokerTruststore = "certs/truststore.jks";
    private String brokerTruststoreType = "JSK";
    private String brokerTruststorePassword = "secret";

    private String brokerKeystore = "certs/clientkeystore.jks";
    private String brokerKeystoreType = "JKS";
    private String brokerKeystorePassword = "secret";

    private String clientJmsBrokerUrl = "ssl://localhost:61616";
}
