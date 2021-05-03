package moe.ofs.backend;

import moe.ofs.backend.connector.Configurable;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * A plugin is an addon to core functionality of Lava. It can be developed an maintained independent of core features
 * of Lava, and it can be enabled or disabled dynamically by end user.
 *
 * The a plugin by default is a Configurable; configuration writing methods can be called to save xml base config
 * to Saved Games folder in Windows platform.
 */
public interface Plugin extends Configurable {

    Set<Plugin> loadedPlugins = new HashSet<>();

    default void load() {
        if(isEnabled())
            register();
    }

    /**
     * Enable a plugin so that it is automatically initialized when background task is started
     * If background task is already running, register immediately
     */
    default void enable() {
        register();
//        if(BackgroundTask.getCurrentTask().isStarted()) {
//            System.out.println("Background Task is running. Init plugin " + getName());
//            init();
//        } else {
//            System.out.println("Background Task is pending. Postpone initialization of " + getName());
//        }
        writeConfiguration("enabled", "true");
    }

    /**
     * Disable a plugin so that it is not initialized when background task is started
     * If background task is already running, unregister immediately
     * If background task is not running,
     */
    default void disable() {
        unregister();
//        if(BackgroundTask.getCurrentTask().isStarted()) {
//            unregister();
//        }
        writeConfiguration("enabled", "false");
    }

    /**
     * Register a plugin to a handler
     */
    void register();

    /**
     * Unregister a plugin from a handler
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
     * Perform data loading for this addon on background task ready.
     * If addon needs to save config xml and add additional data loading behaviors,
     * it should override this method and call Plugin.super.init() method.
     */
    default void init() {}

    default String getVersion() {
        return null;
    }

    default String getAuthor() {
        return null;
    }

    default String getDependencies() {
        return null;
    }

    default String getLocalizedName() {
        return null;
    }

    default String getLocalizedDescription() {
        return null;
    }

    default String getFullName() {
        File file;
        try {
            file = new File(getClass().getProtectionDomain().getCodeSource().getLocation()
                    .toURI());

            if (!file.toString().endsWith(".jar")) {
                return "";
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "";
        }

        try (JarFile jar = new JarFile(file)) {
            InputStream inputStream = jar.getInputStream(jar.getEntry("META-INF/ident"));

            return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines()
                    .collect(Collectors.joining("\n"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        //            throw new RuntimeException("Plugin identification must be specified or auto-generated");
        return null;  // TODO: enforce this later

    }
}
