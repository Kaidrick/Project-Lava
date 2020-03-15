package moe.ofs.backend;

import lombok.SneakyThrows;
import moe.ofs.backend.util.DcsScriptConfigManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

/**
 * Marking interface
 */
public interface Plugin {

    Path configPath = DcsScriptConfigManager.LAVA_DATA_PATH.resolve("config");

    /**
     * Load ofs.backend.plugin classes from ofs.backend.plugin package
     * for each directory, load the class that implements Plugin interface
     */
    static void loadPlugins() throws IOException {

        PluginClassLoader pluginClassLoader = new PluginClassLoader();

        Properties properties = new Properties();
        properties.load(Plugin.class.getResourceAsStream("/enabled_plugins.properties"));

        properties.forEach((pluginName, pluginCoreClassName) ->
                pluginClassLoader.invokeClassMethod(String.format("moe.ofs.backend.plugin.%s.%s",
                        pluginName, pluginCoreClassName)));
    }

    /**
     * Called once to initialize plugin or register a plugin to a handler
     */
    void register();

    /**
     * Unload a plugin or unregister a plugin from a handler
     */
    void unregister();

    /**
     * Provide the display name of plugin
     * @return name
     */
    String getName();

    /**
     * Provide a short description of the plugin
     * @return desc
     */
    String getDescription();

    /**
     * Return a boolean value to indicate whether the plugin is initialized or registered to a handler.
     * In other word, this value represents whether the register() or unregister() has been called.
     * @return
     */
    boolean isLoaded();

    default String getIdent() {
        String[] strings = getClass().getCanonicalName().split("\\.");
        return strings[strings.length - 2];
    }

    // read configuration xml from Saved Games Lava folder
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
            throw new RuntimeException("Unable to locale XML config file for addon \"" + getName() + "\"");
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
                properties.storeToXML(outputStream, "Config XML for Lava addon " + getName());
            }
        } else {  // else write to file directly
            try(OutputStream outputStream = Files.newOutputStream(configXmlPath)) {
                properties.setProperty(key, value);
                properties.storeToXML(outputStream, "Config XML for Lava addon " + getName());
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
                properties.storeToXML(outputStream, "Config XML for Lava addon " + getName());
            }
        } else {  // else write to file directly
            try(OutputStream outputStream = Files.newOutputStream(configXmlPath)) {
                map.forEach(properties::setProperty);
                properties.storeToXML(outputStream, "Config XML for Lava addon " + getName());
            }
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
