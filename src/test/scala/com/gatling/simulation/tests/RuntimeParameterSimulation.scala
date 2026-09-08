package com.gatling.simulation.tests
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

// To run this Simulation With command line use the below command
// mvn gatling:test -Dgatling.simulationClass=com.gatling.simulation.tests.RuntimeParameterSimulation -DUSERS=10 -DRAMP_DURATION=20 -DTEST_DURATION=30
// ./gradlew gatlingRun -Dgatling.simulationClass=com.gatling.simulation.tests.RuntimeParameterSimulation -DUSERS=10 -DRAMP_DURATION=20 -DTEST_DURATION=30
class RuntimeParameterSimulation extends Simulation {
  val httpProtocol = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  def USERCOUNT = System.getProperty("USERS", "5").toInt
  def RAMPDURATION = System.getProperty("RAMP_DURATION", "10").toInt
  def TESTDURATION = System.getProperty("TEST_DURATION", "20").toInt

  before {
    println(s"Running Test With ${USERCOUNT} users")
    println(s"Ramping Users over ${RAMPDURATION} seconds")
    println(s"Total Test Duration is ${TESTDURATION} seconds")
  }
  def getAllJsonPosts(): ChainBuilder = {

    exec(
      http("Get All Json Posts")
        .get("/posts")
        .check(
          bodyString.saveAs("allPostsResponse")
        )
    ).pause(5)
  }
  val scn = scenario("Basic Load Simulation")
    .forever {
        exec(getAllJsonPosts())
    }



  // ---------------------------------------------------------
  // LOAD / INJECTION PROFILE
  // ---------------------------------------------------------

  setUp(
    scn.inject(
        nothingFor(5),
        constantUsersPerSec(USERCOUNT).during(RAMPDURATION),
        rampUsersPerSec(1).to(5).during(TESTDURATION)
      )
  )
    .protocols(httpProtocol)
}
