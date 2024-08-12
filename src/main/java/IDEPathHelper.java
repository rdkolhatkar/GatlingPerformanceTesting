import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.util.Objects.requireNonNull;

public class IDEPathHelper {

    static final Path gradleSourcesDirectory;
    static final Path gradleResourcesDirectory;
    static final Path gradleBinariesDirectory;
    static final Path resultsDirectory;
    static final Path recorderConfigFile;

    static {
        try {
            // Locate the project root directory
            Path projectRootDir = Paths.get(requireNonNull(IDEPathHelper.class.getResource("gatling.conf"), "Couldn't locate gatling.conf").toURI()).getParent().getParent().getParent();

            // Define the Gradle-specific directories
            Path gradleBuildDirectory = projectRootDir.resolve("build");
            Path gradleSrcMainDirectory = projectRootDir.resolve("src").resolve("main");

            gradleSourcesDirectory = gradleSrcMainDirectory.resolve("java");
            gradleResourcesDirectory = gradleSrcMainDirectory.resolve("resources");
            gradleBinariesDirectory = gradleBuildDirectory.resolve("classes").resolve("java").resolve("main");
            resultsDirectory = gradleBuildDirectory.resolve("gatling-results");
            recorderConfigFile = gradleResourcesDirectory.resolve("recorder.conf");
        } catch (URISyntaxException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
