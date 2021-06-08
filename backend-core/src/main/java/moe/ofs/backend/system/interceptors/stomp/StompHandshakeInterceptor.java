package moe.ofs.backend.system.interceptors.stomp;

import moe.ofs.backend.system.model.WebSocketAuthInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class StompHandshakeInterceptor implements HandshakeInterceptor {

    private final Cache websocketAuthCache;

    public StompHandshakeInterceptor(@Qualifier("websocketAuthCacheManager") CacheManager cacheManager) {
        this.websocketAuthCache = cacheManager.getCache("websockets");
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest,
                                   ServerHttpResponse serverHttpResponse,
                                   WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        String oneTimeToken = getOneTimeToken(serverHttpRequest);
        if (oneTimeToken == null) {
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        WebSocketAuthInfo authInfo = websocketAuthCache.get(oneTimeToken, WebSocketAuthInfo.class);
        if (authInfo == null) {
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        websocketAuthCache.evict(oneTimeToken);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }

    /**
     * Extract one-time token passed by the request url from client
     * @param serverHttpRequest the HTTP request that may carry a one-time token
     * @return extracted token from query string
     */
    private String getOneTimeToken(ServerHttpRequest serverHttpRequest) {
        UriComponents uriComponents = UriComponentsBuilder.fromHttpRequest(serverHttpRequest).build();
        return uriComponents.getQueryParams().getFirst("token");
    }
}
