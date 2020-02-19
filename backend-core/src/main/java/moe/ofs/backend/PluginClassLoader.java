package moe.ofs.backend;

import moe.ofs.backend.util.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class PluginClassLoader extends ClassLoader {
    public void invokeClassMethod(String className) {
        try {
            // Create a new JavaClassLoader
            ClassLoader classLoader = this.getClass().getClassLoader();

            // Load the target class using its binary name
            Class loadedMyClass = classLoader.loadClass(className);

//            System.out.println("Loaded class name: " + loadedMyClass.getName());
            List<Class> interfaces = Arrays.asList(loadedMyClass.getInterfaces());

            if(interfaces.contains(Plugin.class)) {
                Constructor constructor = loadedMyClass.getConstructor();
                Object myClassObject = constructor.newInstance();

                // Getting the target method from the loaded class and invoke it using its name
                Method method = loadedMyClass.getMethod("register");
//                System.out.println("Invoked method name: " + method.getName());
                method.invoke(myClassObject);

                Logger.log("Plugin loaded successfully from class: " + className);
            }

        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException |
                NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
