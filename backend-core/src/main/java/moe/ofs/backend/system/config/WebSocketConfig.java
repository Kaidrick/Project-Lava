package moe.ofs.backend.system.config;

import lombok.RequiredArgsConstructor;
import moe.ofs.backend.system.interceptors.stomp.StompHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandshakeInterceptor handshakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("localhost").setRelayPort(61613)
                .setSystemHeartbeatSendInterval(10000)  // 5000
                .setSystemHeartbeatReceiveInterval(10000);  // 10000
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/lava-ws")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("http://localhost:3000", "https://localhost:3000")
                .withSockJS()
                .setHeartbeatTime(10000);
    }
}
