import java.nio.file.Paths

object IDEPathHelper {

  private val projectRootDir =
    Paths.get(System.getProperty("user.dir"))

  private val gradleBuildDirectory =
    projectRootDir.resolve("build")

  private val gradleGatlingClassesDirectory =
    gradleBuildDirectory
      .resolve("classes")
      .resolve("scala")
      .resolve("gatling")

  private val gradleGatlingResourcesDirectory =
    gradleBuildDirectory
      .resolve("resources")
      .resolve("gatling")

  val mavenSourcesDirectory =
    projectRootDir
      .resolve("src")
      .resolve("test")
      .resolve("scala")

  val mavenResourcesDirectory =
    projectRootDir
      .resolve("src")
      .resolve("test")
      .resolve("resources")

  val mavenBinariesDirectory =
    gradleGatlingClassesDirectory

  val resultsDirectory =
    gradleBuildDirectory
      .resolve("gatling")

  val recorderConfigFile =
    mavenResourcesDirectory
      .resolve("recorder.conf")
}