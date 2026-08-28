package com.gatling.simulation.basic

import io.gatling.core.Predef._
import io.gatling.http.Predef._
class GatlingMethodCodeReuseSimuation extends Simulation{
  val httpProtocol = http.baseUrl("https://videogamedb.uk")
    .acceptHeader("application/json")

  // Creating a custom method or function in scala or Gatling
  def getAllVideoGames() = {
    exec(
      http("Get All Video Games")
      .get("/api/videogame")
      .check(status.is(200))
    )
  }

  def getSpecificGame() = {
    exec(
      http("Get Specific Video Games")
        .get("/api/videogame/1")
        .check(status.in(200 to 210 ))
    )
  }

  // Calling the custom methods or functions
  val scn = scenario("Code reuse")
    .exec(
      getAllVideoGames()
    )
    .pause(5)
    .exec(
      getSpecificGame()
    )
    .pause(5)
    .exec(
      getAllVideoGames()
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
