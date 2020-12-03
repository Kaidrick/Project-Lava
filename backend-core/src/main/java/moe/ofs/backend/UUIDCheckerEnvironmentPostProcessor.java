//package moe.ofs.backend;
//
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.io.file.FileReader;
//import cn.hutool.core.io.file.FileWriter;
//import cn.hutool.core.util.IdUtil;
//import cn.hutool.core.util.ObjectUtil;
//import lombok.SneakyThrows;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.env.EnvironmentPostProcessor;
//import org.springframework.boot.system.ApplicationHome;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.core.env.MapPropertySource;
//import org.springframework.stereotype.Component;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class UUIDCheckerEnvironmentPostProcessor implements EnvironmentPostProcessor {
//
//    @SneakyThrows
//    @Override
//    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
//        ApplicationHome ah = new ApplicationHome(BackendApplication.class);
//        String docStorePath = ah.getSource().getParentFile().toString();
//        boolean empty = FileUtil.isEmpty(new File(docStorePath + "/application.properties"));
//        String name;
//        if (empty) {
//            name = "applicationConfig: [classpath:/application.properties]";
//        } else {
//            name = "applicationConfig: [" + docStorePath + "/application.properties" + "]";
//        }
//        MapPropertySource propertySource = (MapPropertySource) environment.getPropertySources().get(name);
//        Map<String, Object> source = propertySource.getSource();
//        Map<String, Object> map = new HashMap<>(source);
//        if (ObjectUtil.isEmpty(source.get("UUID"))) {
//            String uuid = IdUtil.fastSimpleUUID();
//            map.put("UUID", uuid);
//            Object activeFile = source.get("spring.profiles.active");
//
//            FileWriter fileWriter = new FileWriter(docStorePath + "/application.properties");
//            fileWriter.write("UUID=" + uuid);
//            fileWriter.append("\r\nspring.profiles.active=" + activeFile);
//            environment.getPropertySources().replace(name, new MapPropertySource(name, map));
//        }
//    }
//}
