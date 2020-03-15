package moe.ofs.backend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PluginTest {

    Plugin testPlugin;
    Path testPluginXmlPath;
    Map<String, String> configKeyValueMap;

    String enabledKey = "enabled";

    @BeforeEach
    void setUp() {
        // init map
        configKeyValueMap = Stream.of(new String[][] {
                { "motd_ge", "Guten tag!" },
                { "motd_cn", "中文字符测试！" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        // init plugin
        testPlugin = new Plugin() {
            @Override
            public void register() {

            }

            @Override
            public void unregister() {

            }

            @Override
            public String getName() {
                return "Test Plugin Please Ignore";
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public boolean isLoaded() {
                return false;
            }
        };
        testPluginXmlPath = Plugin.configPath.resolve(testPlugin.getName() + ".xml");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testPluginXmlPath);
    }

    @Test
    void writeThenReadConfig() throws IOException {
        testPlugin.writeConfiguration(enabledKey, "true");

        // check size
        try(InputStream inputStream = Files.newInputStream(testPluginXmlPath)) {
            Properties properties = new Properties();
            properties.loadFromXML(inputStream);
            assertEquals(1, properties.size());
        }

        // check value
        String enabledPropertyValue = testPlugin.readConfiguration(enabledKey);
        assertEquals("true", enabledPropertyValue);

        testPlugin.writeConfiguration(configKeyValueMap);
        // check size
        try(InputStream inputStream = Files.newInputStream(Plugin.configPath.resolve(testPluginXmlPath))) {
            Properties properties = new Properties();
            properties.loadFromXML(inputStream);
            assertEquals(3, properties.size());
        }

        assertEquals("Guten tag!", testPlugin.readConfiguration("motd_ge"));
        assertEquals("中文字符测试！", testPlugin.readConfiguration("motd_cn"));
    }

    @Test
    void testWriteAndReadKeyValueConfigToXML() throws IOException {
        testPlugin.writeConfiguration(enabledKey, "true");

        // check size
        try(InputStream inputStream = Files.newInputStream(testPluginXmlPath)) {
            Properties properties = new Properties();
            properties.loadFromXML(inputStream);
            assertEquals(1, properties.size());
        }

        // check value
        String enabledPropertyValue = testPlugin.readConfiguration(enabledKey);
        assertEquals("true", enabledPropertyValue);
    }

    @Test
    void testWriteAndReadMapBasedConfigToXML() throws IOException {
        testPlugin.writeConfiguration(configKeyValueMap);
        // check size
        try(InputStream inputStream = Files.newInputStream(testPluginXmlPath)) {
            Properties properties = new Properties();
            properties.loadFromXML(inputStream);
            assertEquals(2, properties.size());
        }

        assertEquals("Guten tag!", testPlugin.readConfiguration("motd_ge"));
        assertEquals("中文字符测试！", testPlugin.readConfiguration("motd_cn"));
    }

    @Test
    void xmlConfigExists() {
        assertEquals(Files.exists(Plugin.configPath.resolve(testPlugin.getName())), testPlugin.xmlConfigExists());
    }
}