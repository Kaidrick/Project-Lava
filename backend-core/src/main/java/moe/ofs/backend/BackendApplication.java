package moe.ofs.backend;

import moe.ofs.backend.util.HeartbeatThreadFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    private final HeartbeatThreadFactory heartbeatThreadFactory;

    public BackendApplication(HeartbeatThreadFactory heartbeatThreadFactory) {
        this.heartbeatThreadFactory = heartbeatThreadFactory;
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
