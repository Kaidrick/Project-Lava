package moe.ofs.backend.system.interceptors.stomp;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.LavaUserToken;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class StompCommandAuthorityInterceptor implements ChannelInterceptor {

    private final Cache websocketUserSession;


    private final SimpMessageType[] ignoredTypes = {
            SimpMessageType.HEARTBEAT, SimpMessageType.DISCONNECT, SimpMessageType.UNSUBSCRIBE
    };

    public StompCommandAuthorityInterceptor(CacheManager commonCacheManager) {
        this.websocketUserSession = commonCacheManager.getCache("ws-user-sessions");
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // TODO: user role may change in runtime; update cache on authority changes
        LavaUserToken userToken = websocketUserSession.get(accessor.getSessionAttributes().get("sessionId"), LavaUserToken.class);
        String userIdent = userToken.getBaseUserInfoDto().getName();

        // role check
        if (!userToken.getBaseUserInfoDto().getRoles().contains("admin.super_admin")) {
            throw new AccessDeniedException("Only available for super administrator");
        }

        // allow frames with simple commands
        if (Arrays.asList(ignoredTypes).contains(accessor.getMessageType())) {
            return ChannelInterceptor.super.preSend(message, channel);
        }

        // only a single topic is exposed to web client to receive messages
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

            String destination = accessor.getDestination();

            if (destination != null && destination.equals("/topic/frontend.bus")) {
                log.info("trying to subscribe: {}", destination);
            } else {
                log.info("Subscription to {} is not allowed.", destination);
                throw new AccessDeniedException("Subscription is not allowed.");
            }
            // check user authority and destination availability
        } else if (StompCommand.SEND.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();

            // check for allowed topics or queues
            if (destination != null && destination.equals(String.format("/app/frontend.exchange/%s", userIdent))) {
                log.info("trying to send: {}", destination);
            } else {
                log.info("Send to {} is not allowed.", destination);
                throw new AccessDeniedException("Send destination is not allowed.");
            }
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }
}
