package moe.ofs.backend.system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {


    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.UNSUBSCRIBE, SimpMessageType.DISCONNECT)
                .permitAll()

                .simpDestMatchers("/app/**")
                .permitAll()


                .simpSubscribeDestMatchers("/app/**")
                .permitAll()


                .anyMessage()
                .denyAll();

        super.configureInbound(messages);
    }

    // TODO(use a csrf token controller to issue csrf token instead)
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
