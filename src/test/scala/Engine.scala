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
//      "com.gatling.simulation.PauseTimeoutGatlingSimulation"
//      "com.gatling.simulation.GetVideoGamesDbApiSimulation"
//      "com.gatling.simulation.ResponseStatusAndBodyCheckSimulation"
//      "com.gatling.simulation.PrintingSessionLogsGameApiSimulation"
//      "com.gatling.simulation.ListAllVideoGamesDbSimulation"
//      "com.gatling.simulation.GatlingMethodCodeReuseSimuation"
//      "com.gatling.simulation.LoopingHttpCallsWithGatling"
      "com.gatling.simulation.AuthenticatingInGatlingSimulation"
    )

  Gatling.fromMap(props.build)
}