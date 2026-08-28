package com.gatling.simulation.basic

import io.gatling.core.Predef._
import io.gatling.http.Predef._
class BasicTestCustomFeederSimulation extends Simulation{

  val httpProtocol = http
    .baseUrl("https://videogamedb.uk:443")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  var idNumbers = (1 to 10).iterator

  val customFeeder = Iterator.continually(Map("gameId" -> idNumbers.next()))

  def getSpecificGame() = {
    repeat(10){
      feed(customFeeder)
        .exec(http("Get Video Game With ID : #{gameId}")
        .get("/api/videogame/#{gameId}")
        .check(status.is(200)))
        .pause(1)
    }
  }

  val scn = scenario("Basic Custom Feeder")
    .exec(getSpecificGame())


  setUp(
    scn.inject(
      atOnceUsers(1)
    )
  ).protocols(httpProtocol)

}
