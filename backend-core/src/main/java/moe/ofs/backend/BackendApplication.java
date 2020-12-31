package moe.ofs.backend;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.util.HeartbeatThreadFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@Slf4j
public class BackendApplication {
    private static Environment environment;
    private final HeartbeatThreadFactory heartbeatThreadFactory;

    public BackendApplication(HeartbeatThreadFactory heartbeatThreadFactory, Environment environment) {
        this.heartbeatThreadFactory = heartbeatThreadFactory;
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);

        String uuid = environment.getProperty("UUID");
        log.info("******UUID:"+uuid);
        if (StrUtil.isBlank(uuid)) {
            ApplicationHome ah = new ApplicationHome(BackendApplication.class);
            String docStorePath = ah.getSource().getParentFile().toString();
            boolean check = FileUtil.isFile(new File(docStorePath + "/application.properties"));
            if (!check) {
                uuid = IdUtil.fastSimpleUUID();

                FileWriter fileWriter = new FileWriter(docStorePath + "/application.properties");
                fileWriter.write("UUID=" + uuid);
                fileWriter.append("\r\nspring.profiles.active=" + environment.getProperty("spring.profiles.active"));
            }
        }
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
