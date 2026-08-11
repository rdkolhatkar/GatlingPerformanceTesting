import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

object Engine extends App {

  val props = new GatlingPropertiesBuilder()
    .resourcesDirectory(
      IDEPathHelper.mavenResourcesDirectory.toString
    )
    .resultsDirectory(
      IDEPathHelper.resultsDirectory.toString
    )
    .binariesDirectory(
      IDEPathHelper.mavenBinariesDirectory.toString
    )
    .simulationClass(
//      "com.gatling.simulation.ComputerDatabaseSimulation"
//      "com.gatling.simulation.GetAllVideoGamesDbApiSimulation"
      "com.gatling.simulation.GetVideoGamesDbApiSimulation"
    )

  Gatling.fromMap(props.build)
}