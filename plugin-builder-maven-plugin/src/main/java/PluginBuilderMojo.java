import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.FileSet;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Mojo(name = "plugin-builder", defaultPhase = LifecyclePhase.COMPILE,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class PluginBuilderMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(defaultValue = "${project.resources}", required = true, readonly = true)
    private List<Resource> resources;

    @Parameter( defaultValue = "${project.compileSourceRoots}", readonly = true, required = true )
    private List<String> compileSourceRoots;

    private static final String pluginPackage = "moe.ofs.backend.plugin";
    private static final String pluginDirectory = "backend-core/src/main/java/moe/ofs/backend/plugin";

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {

            getLog().info(("compile cp: " +
                    this.project.getCompileClasspathElements()));

            URL[] runtimeUrls = getPackageUrls();

            URLClassLoader classLoader =
                    new URLClassLoader(runtimeUrls, Thread.currentThread().getContextClassLoader());

            // find where META-INF is
            // if absent, create such directory
            String metaInfoRoot = resources.stream().map(FileSet::getDirectory).findAny().get();
//                    .orElseThrow(() -> new RuntimeException("Unable to locate any resource directory"));

            Path metaInf = Files.createDirectories(Paths.get(metaInfoRoot).resolve("META-INF"));

            Path metaInfOutput = Files.createDirectories(Paths.get(
                    project.getCompileClasspathElements().stream()
                            .filter(cp -> cp.endsWith("\\target\\classes") || cp.endsWith("/target/classes"))
                            .findAny()
//                            .get()
                            .orElseThrow(() -> new RuntimeException("Unable to locate class output directory"))
            )).resolve("META-INF");

            List<String> autoConfigurationList = new ArrayList<>();

            compileSourceRoots.stream()
                    .map(Paths::get)
                    .filter(r -> r.getFileName().toString().equals("java"))
                    .forEach(r -> {
                        // walk path and find classes
                        // find class name if is annotated as a spring component
                        try {
                            Files.walk(r)
                                    .filter(j -> j.getFileName().toString().endsWith(".java"))
                                    .forEach(j -> {
                                        String name = j.toString()
                                                // FIXME: bad, really bad
                                                .substring(j.toString().indexOf(
                                                        j.toString().contains("\\") ? "java\\" : "java/"
                                                ) + 5)
                                                .replace(j.toString().contains("\\") ? "\\" : "/", ".");

                                        String className = name.substring(0, name.length() - 5);
                                        try {
                                            Class targetClass = classLoader.loadClass(className);
                                            if(Arrays.stream(targetClass.getAnnotations())
                                                    .map(annotation -> annotation.annotationType().getName())
                                                    .anyMatch(typeName ->
                                                            typeName.contains("org.springframework.stereotype"))) {
                                                // spring context needs to include this one
                                                getLog().info("Found component:" + className);
                                                autoConfigurationList.add(className);
                                            }
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            Path sourceSpringFactory = metaInf.resolve("spring.factories");
            Path outputSpringFactory = metaInfOutput.resolve("spring.factories");

            try(BufferedWriter writer = Files.newBufferedWriter(sourceSpringFactory)) {
                writer.write("org.springframework.boot.autoconfigure.EnableAutoConfiguration=\\\n");
                writer.append(String.join(",\\\n", autoConfigurationList));
            }

            Files.createDirectories(metaInfOutput);
            Files.copy(sourceSpringFactory, outputSpringFactory, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException | DependencyResolutionRequiredException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
    private URL[] getPackageUrls() throws DependencyResolutionRequiredException, MalformedURLException {
        List compileClasspathElements = project.getCompileClasspathElements();
        URL[] runtimeUrls = new URL[compileClasspathElements.size()];
        for (int i = 0; i < compileClasspathElements.size(); i++) {
            String element = (String) compileClasspathElements.get(i);
            runtimeUrls[i] = new File(element).toURI().toURL();
        }
        return runtimeUrls;
    }
}
