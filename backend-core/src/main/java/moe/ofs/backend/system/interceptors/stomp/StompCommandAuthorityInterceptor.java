package moe.ofs.backend.system.interceptors.stomp;

import lombok.extern.slf4j.Slf4j;
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

    private final SimpMessageType[] ignoredTypes = {
            SimpMessageType.HEARTBEAT, SimpMessageType.DISCONNECT, SimpMessageType.UNSUBSCRIBE
    };

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (Arrays.asList(ignoredTypes).contains(accessor.getMessageType())) {
            return ChannelInterceptor.super.preSend(message, channel);
        }

        List<String> lavaUserIdentHeader = accessor.getNativeHeader("lava-user-ident");
        if (lavaUserIdentHeader == null) {
            throw new AccessDeniedException("Missing Lava user identification in header");
        }

        String ident = lavaUserIdentHeader.get(lavaUserIdentHeader.size() - 1);

        // TODO: allow root at the moment
        if (!ident.equals("root")) {
            throw new AccessDeniedException("Only available for super administrator");
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

            if (destination != null && destination.equals("/app/frontend.exchange")) {
                log.info("trying to send: {}", destination);
            } else {
                log.info("Send to {} is not allowed.", destination);
                throw new AccessDeniedException("Send destination is not allowed.");
            }
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }
}
