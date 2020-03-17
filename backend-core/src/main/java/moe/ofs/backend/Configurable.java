package moe.ofs.backend;

import lombok.SneakyThrows;
import moe.ofs.backend.util.DcsScriptConfigManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

public interface Configurable {

    String getName();

    Path configPath = DcsScriptConfigManager.LAVA_DATA_PATH.resolve("config");

    /**
     * Read configuration xml from Saved Games Lava folder
     * If such xml config file does not exist for this addon, throw a new NoSuchFileException
     * @param key config key
     * @return value corresponding to the config key
     */
    @SneakyThrows
    default String readConfiguration(String key) {
        Path configXmlPath = configPath.resolve(getName() + ".xml");

        Properties properties = new Properties();

        if(xmlConfigExists()) {  // if xml file of this plugin name exists, read then write to xml
            try(InputStream inputStream = Files.newInputStream(configXmlPath)) {
                properties.loadFromXML(inputStream);
                return properties.getProperty(key);
            }
        } else {  // else write to file directly
            throw new NoSuchFileException("Unable to locale XML config file for \"" + getName() + "\"");
        }
    }

    /**
     * Read then write to configuration xml
     * if xml file exists, read the file to Properties, set new property and then write to xml file
     */
    @SneakyThrows
    default void writeConfiguration(String key, String value) {
        if(Files.notExists(configPath))
            Files.createDirectories(configPath);

        Path configXmlPath = configPath.resolve(getName() + ".xml");

        Properties properties = new Properties();

        if(xmlConfigExists()) {  // if xml file of this plugin name exists, read then write to xml
            try(InputStream inputStream = Files.newInputStream(configXmlPath)) {
                properties.loadFromXML(inputStream);
                properties.setProperty(key, value);
            }

            try(OutputStream outputStream = Files.newOutputStream(configXmlPath)) {
                properties.storeToXML(outputStream, "Config XML for " + getName());
            }
        } else {  // else write to file directly
            try(OutputStream outputStream = Files.newOutputStream(configXmlPath)) {
                properties.setProperty(key, value);
                properties.storeToXML(outputStream, "Config XML for " + getName());
            }
        }
    }

    /**
     * Write map based configuration to a *.xml file.
     * Create ./Lava/Config if this directory does not exists yet
     * If xml file already exists, read the file to property, set new property and then write to xml file.
     * @param map a Map containing String-String pairs as configuration keys and values.
     */
    @SneakyThrows
    default void writeConfiguration(Map<String, String> map) {
        if(Files.notExists(configPath))
            Files.createDirectories(configPath);

        Path configXmlPath = configPath.resolve(getName() + ".xml");

        Properties properties = new Properties();

        if(xmlConfigExists()) {  // if xml file of this plugin name exists, read then write to xml
            try(InputStream inputStream = Files.newInputStream(configXmlPath)) {
                properties.loadFromXML(inputStream);
                map.forEach(properties::setProperty);
            }

            try(OutputStream outputStream = Files.newOutputStream(configXmlPath)) {
                properties.storeToXML(outputStream, "Config XML for " + getName());
            }
        } else {  // else write to file directly
            try(OutputStream outputStream = Files.newOutputStream(configXmlPath)) {
                map.forEach(properties::setProperty);
                properties.storeToXML(outputStream, "Config XML for " + getName());
            }
        }
    }

    /**
     * Read enabled property from the config xml for the this addon.
     * If there is no config xml file for this plugin, create new xml and write property enabled to with value of false.
     * If there is no such property as "enabled", write this property with value of "false" to initialize its state.
     * @return boolean value indicating whether enabled property string is set to true
     */
    default boolean isEnabled() {
        if(xmlConfigExists()) {
            String value = readConfiguration("enabled");
            if(value == null) {
                writeConfiguration("enabled", "false");
                return false;
            } else {
                return Boolean.parseBoolean(value);
            }
        } else {
            writeConfiguration("enabled", "false");
            return false;
        }
    }

    /**
     * Used to check whether a xml file corresponding to this plugin exists.
     * @return boolean value indicating whether this file exists in the file system.
     */
    default boolean xmlConfigExists() {
        return Files.exists(configPath.resolve(getName() + ".xml"));
    }
}
