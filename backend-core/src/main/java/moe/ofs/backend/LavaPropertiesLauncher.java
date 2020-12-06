package moe.ofs.backend;

import org.springframework.boot.loader.PropertiesLauncher;
import org.springframework.boot.loader.jar.JarFile;

public class LavaPropertiesLauncher extends PropertiesLauncher  {

    public static void main(String[] args) throws Exception {
        LavaPropertiesLauncher launcher = new LavaPropertiesLauncher();
        args = launcher.getArgs(args);
        launcher.launch(args);
    }

    @Override
    protected void launch(String[] args) throws Exception {
        if (!this.isExploded()) {
            JarFile.registerUrlProtocolHandler();
        }

        ClassLoader classLoader = this.createClassLoader(this.getClassPathArchivesIterator());
//        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        String jarMode = System.getProperty("jarmode");
        String launchClass = jarMode != null && !jarMode.isEmpty() ? "org.springframework.boot.loader.jarmode.JarModeLauncher" : this.getMainClass();
        this.launch(args, launchClass, classLoader);
    }
}
