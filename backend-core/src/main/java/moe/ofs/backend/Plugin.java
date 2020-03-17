package moe.ofs.backend;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * A plugin is an addon to core functionality of Lava. It can be developed an maintained independent of core features
 * of Lava, and it can be enabled or disabled dynamically by end user.
 *
 * The a plugin by default is a Configurable; configuration writing methods can be called to save xml base config
 * to Saved Games folder in Windows platform.
 */
public interface Plugin extends Configurable {

    Set<Plugin> loadedPlugins = new HashSet<>();

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
     * @return name of the plugin
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

    /**
     * Load the addon. If addon needs to save config xml and add additional data loading behaviors,
     * it should override this method and call Plugin.super.init() method.
     * If
     */
    default void init() {
        if(isEnabled()) {
            register();
            writeConfiguration("enabled", "true");
        }
        loadedPlugins.add(this);
    }
}
