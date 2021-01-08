package moe.ofs.backend.connector;

import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.io.*;
import java.lang.reflect.Type;
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
        return readConfiguration(getName(), key);
    }

    @SneakyThrows
    default String readConfiguration(String configFileName, String key) {
        Path configXmlPath = configPath.resolve(configFileName + ".xml");

        Properties properties = new Properties();

        if(xmlConfigExists(configFileName)) {  // if xml file of this plugin name exists, read then write to xml
            try(InputStream inputStream = Files.newInputStream(configXmlPath)) {
                properties.loadFromXML(inputStream);
                return properties.getProperty(key);
            }
        } else {  // else write to file directly
            throw new NoSuchFileException("Unable to locale XML config file for \"" + configFileName + "\"");
        }
    }

    /**
     * Read then write to configuration xml
     * if xml file exists, read the file to Properties, set new property and then write to xml file
     */
    @SneakyThrows
    default void writeConfiguration(String key, String value) {
        writeConfiguration(getName(), key, value);
    }

    @SneakyThrows
    default void writeConfiguration(String configFileName, String key, String value) {
        if(Files.notExists(configPath))
            Files.createDirectories(configPath);

        Path configXmlPath = configPath.resolve(configFileName + ".xml");

        Properties properties = new Properties();

        if(xmlConfigExists(configFileName)) {  // if xml file of this plugin name exists, read then write to xml
            try(InputStream inputStream = Files.newInputStream(configXmlPath)) {
                properties.loadFromXML(inputStream);
                properties.setProperty(key, value);
            }

            try(OutputStream outputStream = Files.newOutputStream(configXmlPath)) {
                properties.storeToXML(outputStream, "Config XML for " + configFileName);
            }
        } else {  // else write to file directly
            try(OutputStream outputStream = Files.newOutputStream(configXmlPath)) {
                properties.setProperty(key, value);
                properties.storeToXML(outputStream, "Config XML for " + configFileName);
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
        return xmlConfigExists(getName());
    }

    default boolean xmlConfigExists(String configFileName) {
        return Files.exists(configPath.resolve(configFileName + ".xml"));
    }


    @SneakyThrows
    default <T extends Serializable> void writeFile(T object, String fileName) {
        Path configFilePath = configPath.resolve(fileName + ".data");
        FileOutputStream fileOutputStream = new FileOutputStream(configFilePath.toFile());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
    }

    default <T extends Serializable> void writeFile(T object) {
        writeFile(object, getName());
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    default <T extends Serializable> T readFile(String fileName) {
        ClassLoader boot = getClass().getClassLoader();
        Path configFilePath = configPath.resolve(fileName + ".data");
        FileInputStream fileInputStream = new FileInputStream(configFilePath.toFile());
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                try {
                    return Class.forName(desc.getName(), false, ClassLoader.getSystemClassLoader());
                } catch (ClassNotFoundException e) {
                    return Class.forName(desc.getName(), false, boot);
                }
            }
        };
        return (T) objectInputStream.readObject();
    }

    default <T extends Serializable> T readFile() {
        return readFile(getName());
    }

    // TODO --> write json file
    @SneakyThrows
    default <T> void writeJsonFile(T object, String fileName) {
        Path configFilePath = configPath.resolve(fileName + ".json");
        Gson gson = new Gson();

        try(PrintWriter out = new PrintWriter(configFilePath.toFile())) {
            out.println(gson.toJson(object));
        }
    }

    @SneakyThrows
    default <T> T readJsonFile(Type type, String fileName) {
        Path configFilePath = configPath.resolve(fileName + ".json");
        Gson gson = new Gson();
        String s = String.join("\n", Files.readAllLines(configFilePath));

        return gson.fromJson(s, type);
    }


    default boolean dataFileExists() {
        return Files.exists(configPath.resolve(getName() + ".data"));
    }

    default boolean dataFileExists(String fileName) {
        return Files.exists(configPath.resolve(fileName + ".data"));
    }
}
