package moe.ofs.backend.system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;

@Slf4j
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    private final List<ChannelInterceptor> interceptors;
    private final Cache websocketUserSession;

    public WebSocketSecurityConfig(List<ChannelInterceptor> interceptors,
                                   @Qualifier("commonCacheManager") CacheManager wsCacheManager) {
        this.interceptors = interceptors;
        this.websocketUserSession = wsCacheManager.getCache("ws-user-sessions");
    }

    // TODO(use a csrf token controller to issue csrf token instead)
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    protected void customizeClientInboundChannel(ChannelRegistration registration) {
        ChannelInterceptor[] array = interceptors.toArray(new ChannelInterceptor[0]);
        registration.interceptors(array);
    }

    @EventListener
    public void handleSessionClosedEvent(SessionDisconnectEvent event) {
        websocketUserSession.evict(event.getSessionId());
    }
}
