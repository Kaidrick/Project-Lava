package moe.ofs.backend;

import moe.ofs.backend.util.HeartbeatThreadFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("moe.ofs.backend.dao")
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

//            System.out.println("Let's inspect the beans provided by Spring Boot:");

//            String[] beanNames = ctx.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            for (String beanName : beanNames) {
//                System.out.println(beanName);
//            }

            heartbeatThreadFactory.getHeartbeatThread().start();
        };
    }
}
