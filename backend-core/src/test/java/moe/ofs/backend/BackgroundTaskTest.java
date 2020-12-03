package moe.ofs.backend;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.object.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
}