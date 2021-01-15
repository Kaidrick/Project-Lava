package moe.ofs.backend;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.util.HeartbeatThreadFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

//@SpringBootApplication
//@EnableScheduling
@Slf4j
public class BackendApplication {
    private final HeartbeatThreadFactory heartbeatThreadFactory;

    public BackendApplication(HeartbeatThreadFactory heartbeatThreadFactory) {
        this.heartbeatThreadFactory = heartbeatThreadFactory;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    //    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            LavaLog.getLogger(this.getClass())
                    .info("Project Lava initialization finished; starting background heartbeat checker");
            heartbeatThreadFactory.getHeartbeatThread().start();
        };
    }
}
