package moe.ofs.backend;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.connector.DcsScriptConfigManager;
import moe.ofs.backend.dao.AdminInfoDao;
import moe.ofs.backend.dao.UserGroupDao;
import moe.ofs.backend.domain.AdminInfo;
import moe.ofs.backend.domain.UserGroup;
import moe.ofs.backend.util.HeartbeatThreadFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.DigestUtils;

import java.util.Date;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableScheduling
@EnableCaching
@Slf4j
public class LavaApplication {
    private final HeartbeatThreadFactory heartbeatThreadFactory;
    private static AdminInfoDao adminInfoDao;
    private static UserGroupDao userGroupDao;

    public LavaApplication(HeartbeatThreadFactory heartbeatThreadFactory, AdminInfoDao adminInfoDao, UserGroupDao userGroupDao) {
        this.heartbeatThreadFactory = heartbeatThreadFactory;
        this.adminInfoDao = adminInfoDao;
        this.userGroupDao = userGroupDao;
    }

    public static void main(String[] args) {
        SpringApplication.run(LavaApplication.class, args);
        generatorRootKey();
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            LavaLog.getLogger(this.getClass())
                    .info("Project Lava initialization finished; starting background heartbeat checker");
            heartbeatThreadFactory.getHeartbeatThread().start();
        };
    }

    private static void generatorRootKey() {
        Integer integer = adminInfoDao.selectCount(null);
        if (integer != 0) return;
        AdminInfo adminInfo = new AdminInfo();
        adminInfo.setName("root");
        String s = IdUtil.fastSimpleUUID();
        adminInfo.setPassword(DigestUtils.md5DigestAsHex(s.getBytes()));
        adminInfo.setCreateTime(new Date());
        adminInfoDao.insert(adminInfo);
        UserGroup userGroup = new UserGroup();
        userGroup.setGroupId(1L);
        userGroup.setUserId(adminInfo.getId());
        userGroupDao.insert(userGroup);
        String msg = "自动生成的超管密码：" + s;
        log.info("******" + msg);

        String path = DcsScriptConfigManager.LAVA_DATA_PATH.resolve("config").toString();
        FileWriter fileWriter = new FileWriter(path + "/password.txt");
        fileWriter.write(msg);
    }
}
