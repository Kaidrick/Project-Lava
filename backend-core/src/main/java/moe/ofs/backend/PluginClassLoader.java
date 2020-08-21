package moe.ofs.backend;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PluginClassLoader extends ClassLoader {

    public static Set<Plugin> loadedPluginSet = new HashSet<>();

    public void invokeClassMethod(String className) {
        try {
            // Create a new JavaClassLoader
            ClassLoader classLoader = this.getClass().getClassLoader();

            // Load the target class using its binary name
            Class loadedPluginClass = classLoader.loadClass(className);

//            System.out.println("Loaded class name: " + loadedPluginClass.getName());
            List<Class> interfaces = Arrays.asList(loadedPluginClass.getInterfaces());

            if(interfaces.contains(Plugin.class)) {
                Constructor<Plugin> constructor = loadedPluginClass.getConstructor();
                Plugin pluginInstance = constructor.newInstance();

                // Getting the target method from the loaded class and invoke it using its name
                Method method = loadedPluginClass.getMethod("register");
//                System.out.println("Invoked method name: " + method.getName());
                method.invoke(pluginInstance);

                System.out.println("Plugin loaded successfully from class: " + className);

                loadedPluginSet.add(pluginInstance);
            }

        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException |
                NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
