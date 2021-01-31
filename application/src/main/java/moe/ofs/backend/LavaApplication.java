package moe.ofs.backend;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.util.HeartbeatThreadFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableScheduling
@Slf4j
public class LavaApplication {
    private final HeartbeatThreadFactory heartbeatThreadFactory;

    public LavaApplication(HeartbeatThreadFactory heartbeatThreadFactory) {
        this.heartbeatThreadFactory = heartbeatThreadFactory;
    }

    public static void main(String[] args) {
        SpringApplication.run(LavaApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            LavaLog.getLogger(this.getClass())
                    .info("Project Lava initialization finished; starting background heartbeat checker");
            heartbeatThreadFactory.getHeartbeatThread().start();
        };
    }
}
