package moe.ofs.backend.function.unitwiselog.eventlogger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@Configuration
@EnableJms
public class LoggerConfiguration {

    @Bean
    public SpawnControlLogger spawnControlLogger() {
        return new SpawnControlLogger();
    }

    @Bean
    public PlayerConnectionLogger playerConnectionLogger() {
        return new PlayerConnectionLogger();
    }
}
