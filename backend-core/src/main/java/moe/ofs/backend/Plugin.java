package moe.ofs.backend;

import moe.ofs.backend.gui.PluginListCell;

import java.io.IOException;
import java.util.Properties;

/**
 * Marking interface
 */
public interface Plugin {
    /**
     * Load ofs.backend.plugin classes from ofs.backend.plugin package
     * for each directory, load the class that implements Plugin interface
     */
    static void loadPlugins() throws IOException {

        PluginClassLoader pluginClassLoader = new PluginClassLoader();

        Properties properties = new Properties();
        properties.load(BackendMain.class.getResourceAsStream("/enabled_plugins.properties"));

        properties.forEach((pluginName, pluginCoreClassName) ->
                pluginClassLoader.invokeClassMethod(String.format("moe.ofs.backend.plugin.%s.%s",
                        pluginName, pluginCoreClassName)));
    }

    PluginListCell getPluginListCell();
    void setPluginListCell(PluginListCell cell);

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
    };
}
