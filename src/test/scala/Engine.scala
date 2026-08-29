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
//      "com.gatling.simulation.basic.ComputerDatabaseSimulation"
//      "com.gatling.simulation.basic.PauseTimeoutGatlingSimulation"
//      "com.gatling.simulation.basic.GetVideoGamesDbApiSimulation"
//      "com.gatling.simulation.basic.ResponseStatusAndBodyCheckSimulation"
//      "com.gatling.simulation.basic.PrintingSessionLogsGameApiSimulation"
//      "com.gatling.simulation.basic.ListAllVideoGamesDbSimulation"
//      "com.gatling.simulation.basic.GatlingMethodCodeReuseSimuation"
//      "com.gatling.simulation.basic.LoopingHttpCallsWithGatling"
//      "com.gatling.simulation.basic.AuthenticatingInGatlingSimulation"
//      "com.gatling.simulation.basic.GatlingCsvFeederSimulation"
//      "com.gatling.simulation.basic.BasicTestCustomFeederSimulation"
//      "com.gatling.simulation.basic.ComplexCustomFeederSimulation"
//      "com.gatling.simulation.basic.CustomJsonFeederSimulation"
//      "com.gatling.simulation.tests.BasicLoadSimulation"
//      "com.gatling.simulation.tests.GatlingCsvFeederJwtApiSimulation"
      "com.gatling.simulation.tests.CustomJsonFeederJsonPlaceholderSimulation"
    )

  Gatling.fromMap(props.build)
}