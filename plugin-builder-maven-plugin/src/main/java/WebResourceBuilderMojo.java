import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.util.List;

@Mojo(name = "web-builder", defaultPhase = LifecyclePhase.VALIDATE,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class WebResourceBuilderMojo extends AbstractMojo {

    @Parameter(defaultValue = "web", required = true, readonly = true)
    private String webContentPath;  // from project or module root

    @Parameter
    private String webFrameworkMode;  // type of web framework of the web project, used to determine build param

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(defaultValue = "${project.resources}", required = true, readonly = true)
    private List<Resource> resources;

    @Parameter( defaultValue = "${project.compileSourceRoots}", readonly = true, required = true )
    private List<String> compileSourceRoots;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (webFrameworkMode != null) {
            if (webFrameworkMode.toLowerCase().equals("vue")) {
                getLog().info("modify Vue configuration file, call npm to build and rollback " +
                        "changes, before packaging");

                // get vue.config.js
            } else {
                throw new RuntimeException("Not Supported Framework Build Process");
            }
        } else {  // non-build mode; direct copy to specific path
            getLog().info("OK direct copy resource to " + String.format("%s-%s-%s",
                    project.getGroupId(), project.getArtifactId(), project.getVersion()));
        }
    }
}
