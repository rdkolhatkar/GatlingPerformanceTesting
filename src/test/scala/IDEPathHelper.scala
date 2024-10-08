import java.nio.file.{Path, Paths}
import java.util.Objects.requireNonNull

object IDEPathHelper {

  private val projectRootDir = Paths.get(requireNonNull(getClass.getResource("gatling.conf"), "Couldn't locate gatling.conf").toURI).getParent.getParent.getParent.getParent
  private val gradleBuildDirectory = projectRootDir.resolve("build")
  private val gradleSrcDirectory = projectRootDir.resolve("src").resolve("test")

  val gradleSourcesDirectory: Path = gradleSrcDirectory.resolve("scala")
  val gradleResourcesDirectory: Path = gradleSrcDirectory.resolve("resources")
  val gradleBinariesDirectory: Path = gradleBuildDirectory.resolve("classes").resolve("scala").resolve("test")
  val resultsDirectory: Path = gradleBuildDirectory.resolve("reports").resolve("test")
  val recorderConfigFile: Path = gradleResourcesDirectory.resolve("recorder.conf")
}