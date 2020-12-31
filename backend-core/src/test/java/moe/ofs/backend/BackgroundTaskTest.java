package moe.ofs.backend;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.object.*;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class BackgroundTaskTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void gsonDeserializationTest() {
        Gson gson = new Gson();
        Unit unit = new Unit.UnitBuilder()
                .setPayload(new Payload())
                .setCategory(Unit.Category.AIRPLANE)
                .setName("test unit")
                .setSkill(Unit.Skill.HIGH)
                .setType("weird")
                .setSpeed(1234.125)
                .build();
        Group group = new Group.GroupBuilder()
                .setFrequency(123.123)
                .addUnit(unit)
                .setUncontrolled(false)
                .setRoute(new Route())
                .build();

        String testJsonString = gson.toJson(group);
        System.out.println(testJsonString);

        Group reverseGroup = gson.fromJson(testJsonString, Group.class);
        System.out.println("reverseGroup = " + reverseGroup);
        reverseGroup.getUnits().forEach(item -> System.out.println(item.getName()));
    }

    @Test
    void test2() {
        List<String> a = new ArrayList<>();
        a.add("AAA");
        a.add("AA=1");
        log.info("******"+a.contains("AA="));
    }

    @Test
    void runLavaTest() throws IOException {
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-jar");
        command.add("/home/kaidrick/IdeaProjects/Project-Lava/backend-core/target/backend-core-0.0.1-SNAPSHOT.jar");
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Process process = processBuilder.start();

        executorService.submit(() -> {
            try {
                OutputStream processOutput = process.getOutputStream();
                InputStream processInput = process.getInputStream();
                BufferedReader processReader = new BufferedReader(new InputStreamReader(
                        processInput));
                BufferedWriter processWriter = new BufferedWriter(new OutputStreamWriter(
                        processOutput));

                String line;
                while ((line = processReader.readLine()) != null) {
                    System.out.println(line);
                }
                processReader.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        int k = 0;
        while (true) {
            k++;
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (k > 10) {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(null, headers);
                ResponseEntity<String> s = restTemplate.exchange("http://localhost:8080/actuator/shutdown", HttpMethod.POST, entity, String.class);
                System.out.println("s = " + s);
                process.destroy();
                executorService.shutdown();
                System.out.println("break");
                break;
            }
        }
    }
}