package moe.ofs.backend.util;

import moe.ofs.backend.Plugin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class PluginBaker {
//    @SuppressWarnings("unchecked")
    public static void bake() throws IOException {

//        Path pluginPath = BackendMain.resourceToPath(Plugin.class.getResource("plugin"));

        Path pluginPath = Paths.get("backend-core/src/main/java/moe/ofs/backend/plugin");
        Properties prop = new Properties();  // path=main class name

        List<Path> pluginList = Files.walk(pluginPath)
                .filter(c -> c.toString().endsWith(".java") || c.toString().endsWith(".class"))
                .collect(Collectors.toList());

        System.out.println("Bake " + Files.list(pluginPath).count() + " plugins with classes: "
                + pluginList.stream().map(e -> e.getFileName().toString().replace(".java", ""))
                .collect(Collectors.joining(", ")));


        ClassLoader classLoader = PluginBaker.class.getClassLoader();

        pluginList.forEach(p -> {
            try {
                String pluginName = p.getName((p.getNameCount() - 2)).toString();
                String pluginCoreClassName = p.getFileName().toString().replace(".java", "");
                Class loadedMyClass = classLoader.loadClass("moe.ofs.backend.plugin."
                        + pluginName + "."
                        + pluginCoreClassName);

//            System.out.println("Loaded class name: " + loadedMyClass.getName());
                List<Class> interfaces = Arrays.asList(loadedMyClass.getInterfaces());

                if(interfaces.contains(Plugin.class)) {
                    System.out.println("Plugin <" + pluginName + "> validated successfully from class: " + loadedMyClass.getSimpleName());
                    prop.setProperty(pluginName, pluginCoreClassName);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        System.out.println(prop);
        Path path = Paths.get("backend-core/src/main/resources/enabled_plugins.properties");
        try(FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
            prop.store(fileOutputStream, "Enabled Plugins");
        }

    }

    public static void main(String[] args) throws IOException {
        bake();
    }
}
