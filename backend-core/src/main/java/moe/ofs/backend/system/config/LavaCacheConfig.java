package moe.ofs.backend.system.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class LavaCacheConfig {

    @Bean
    public CacheManager websocketAuthCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Collections.singletonList("websockets"));
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterAccess(10, TimeUnit.SECONDS));
        return cacheManager;
    }

    @Primary
    @Bean
    public CacheManager commonCacheManager() {
        return new ConcurrentMapCacheManager("accessToken", "ws-user-sessions");
    }
}
