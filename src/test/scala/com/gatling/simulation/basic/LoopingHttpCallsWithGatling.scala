package com.gatling.simulation.basic

import io.gatling.core.Predef._
import io.gatling.http.Predef._
class LoopingHttpCallsWithGatling extends Simulation {
  val httpProtocol = http.baseUrl("https://videogamedb.uk")
    .acceptHeader("application/json")

  // Run the getAllVideoGames Api call for 3 times
  def getAllVideoGames() = {
    repeat(3){
      exec(
        http("Get All Video Games")
          .get("/api/videogame")
          .check(status.is(200))
      )
    }
  }

  def getSpecificGame() = {
    repeat(5, "counter"){
      exec(
        http("Get Specific Video Game with id: #{counter}")
          .get("/api/videogame/#{counter}")
          .check(status.in(200 to 210 ))
      )
    }
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
    .repeat(2){
        getAllVideoGames() // This call will basically run for 6 Times, because "getAllVideoGames" have repeating loop for 3 iterations, So it's 3 * 2 = 6
    }


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
