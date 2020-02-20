package moe.ofs.backend;

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
}
