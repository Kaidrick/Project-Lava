package moe.ofs.backend.system.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:lava-async-config.properties", encoding = "UTF-8")
@ConfigurationProperties(prefix = "lava.async.thread-pool")
@Getter
@Setter
public class LavaAsyncProperties {
    private int coreSize = 10;
    private int maxSize = 50;
    private int queueCapacity = 500;
    private boolean isDaemon = false;
    private String threadNamePrefix = "lava-service-worker-";
}
