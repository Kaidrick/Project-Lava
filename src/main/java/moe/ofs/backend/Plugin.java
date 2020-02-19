package moe.ofs.backend;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Marking interface
 */
public interface Plugin {
    /**
     * Load ofs.backend.plugin classes from ofs.backend.plugin package
     * for each directory, load the class that implements Plugin interface
     */
    static void loadPlugins() throws IOException, URISyntaxException {
        Path pluginPath = BackendMain.resourceToPath(BackendMain.class.getResource("plugin"));

        List<Path> pluginList = Files.walk(pluginPath)
                .filter(c -> c.toString().endsWith(".java") || c.toString().endsWith(".class"))
                .collect(Collectors.toList());

        Logger.log("Found " + Files.list(pluginPath).count() + " plugins with classes: "
                + pluginList.stream().map(e -> e.getFileName().toString().replace(".class", ""))
                .collect(Collectors.joining(", ")));

        PluginClassLoader pluginClassLoader = new PluginClassLoader();

        pluginList.forEach(
                p -> pluginClassLoader.invokeClassMethod(
                        "moe.ofs.backend.plugin" + "." +
                                p.getName(p.getNameCount() - 2) + "." +
                                p.getFileName().toString()
                                        .replace(".java", "")
                                        .replace(".class", "")
                )
        );
    }
}
