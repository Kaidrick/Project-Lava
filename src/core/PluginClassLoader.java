package core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PluginClassLoader extends ClassLoader {
    public void invokeClassMethod(String className, String methodName) {
        try {
            // Create a new JavaClassLoader
            ClassLoader classLoader = this.getClass().getClassLoader();

            // Load the target class using its binary name
            Class loadedMyClass = classLoader.loadClass(className);

//            System.out.println("Loaded class name: " + loadedMyClass.getName());

            // Create a new instance from the loaded class
            Constructor constructor = loadedMyClass.getConstructor();
            Object myClassObject = constructor.newInstance();

            if(!(myClassObject instanceof Plugin)) {

            } else {
                // Getting the target method from the loaded class and invoke it using its name
                Method method = loadedMyClass.getMethod(methodName);
//                System.out.println("Invoked method name: " + method.getName());
                method.invoke(myClassObject);

                Logger.log("Plugin loaded successfully: " + className);
            }

        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException |
                NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
