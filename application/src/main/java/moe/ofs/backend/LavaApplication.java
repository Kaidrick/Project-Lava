package moe.ofs.backend;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.aspects.LuaInteractPremiseAspect;
import moe.ofs.backend.util.HeartbeatThreadFactory;
import moe.ofs.backend.util.LuaInteract;
import org.aspectj.lang.Aspects;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class LavaApplication {
    private static Environment environment;
    private final HeartbeatThreadFactory heartbeatThreadFactory;

    public LavaApplication(HeartbeatThreadFactory heartbeatThreadFactory, Environment environment) {
        this.heartbeatThreadFactory = heartbeatThreadFactory;
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(LavaApplication.class, args);

        String uuid = environment.getProperty("UUID");
        log.info("******UUID:"+uuid);
        if (StrUtil.isBlank(uuid)) {
            ApplicationHome ah = new ApplicationHome(LavaApplication.class);
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

    @LuaInteract
    @Scheduled(fixedDelay = 100L)
    public void pingTest() {
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&77");
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&77");
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&77");
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&77");
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
