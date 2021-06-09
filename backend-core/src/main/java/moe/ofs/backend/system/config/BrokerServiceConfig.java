package moe.ofs.backend.system.config;

import lombok.RequiredArgsConstructor;
import moe.ofs.backend.system.properties.ActiveMQProperties;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.SslBrokerService;
import org.apache.activemq.broker.SslContext;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.transport.TransportServer;
import org.apache.activemq.transport.wss.WSSTransportServer;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.net.ssl.*;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class BrokerServiceConfig {

//    private final ActiveMQProperties activeMQProperties;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public BrokerService broker() throws Exception {
        SslBrokerService broker = new SslBrokerService();
        broker.setBrokerName("embedded-broker-service");
        broker.setPersistent(false);
        broker.addConnector("tcp://localhost:61616");
        broker.addConnector("vm://embedded-broker?broker.persistent=false");
        broker.addConnector("stomp://localhost:61613");
//        broker.addConnector("ws://localhost:61618/notting");
//        broker.addConnector("wss://localhost:61619");

//        broker.addSslConnector(activeMQProperties.getBrokerUrl(), readKeystore(), readTruststore(), null);
//
//        SslContext sslContext = new SslContext();
//        sslContext.setKeyManagers(Arrays.asList(readKeystore()));
//        sslContext.setTrustManagers(Arrays.asList(readTruststore()));
//
//        broker.setSslContext(sslContext);

        return broker;
    }

//    private KeyManager[] readKeystore() throws Exception {
//        final KeyManagerFactory
//                theKeyManagerFactory
//                = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//        final KeyStore theKeyStore = KeyStore.getInstance(activeMQProperties.getBrokerKeystoreType());
//
//        final Resource theKeystoreResource = new ClassPathResource(activeMQProperties.getBrokerKeystore());
//        theKeyStore.load(theKeystoreResource.getInputStream(),
//                activeMQProperties.getBrokerKeystorePassword().toCharArray());
//        theKeyManagerFactory.init(theKeyStore, activeMQProperties.getBrokerKeystorePassword().toCharArray());
//        return theKeyManagerFactory.getKeyManagers();
//    }
//
//    private TrustManager[] readTruststore() throws Exception {
//        final KeyStore theTruststore = KeyStore.getInstance(activeMQProperties.getBrokerTruststoreType());
//
//        final Resource theTruststoreResource = new ClassPathResource(activeMQProperties.getBrokerTruststore());
//        theTruststore.load(theTruststoreResource.getInputStream(), null);
//        final TrustManagerFactory theTrustManagerFactory
//                = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//        theTrustManagerFactory.init(theTruststore);
//        return theTrustManagerFactory.getTrustManagers();
//    }
}
