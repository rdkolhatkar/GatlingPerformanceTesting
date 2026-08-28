package com.gatling.simulation.basic

import io.gatling.core.Predef._
import io.gatling.http.Predef._
class GetVideoGamesDbApiSimulation extends Simulation{

  // 1) Http Configuration
  // Defines the base URL that will be used for all HTTP requests.
  // Also specifies that we expect the response in JSON format.
  val httpProtocol = http.baseUrl("https://videogamedb.uk")
    .acceptHeader("application/json")

  // 2) Scenario Definition
  // Creates a scenario named "VideoGame DB Test".
  // The scenario sends a GET request to retrieve all video games.
  val scn = scenario("VideoGame DB Test")
        .exec(http("Get All Games")
        .get("/api/videogame")
        .check(status.saveAs("responseStatus")) // Prints the HTTP response status code to the console. Example: 200, 404, 500, etc.
        .check(bodyString.saveAs("responseBody")))
        // Executes a block of Scala code after the HTTP request is completed.
        // The saved status and response body are retrieved from the Gatling session and printed to the console.
        .exec { session =>
          println("========================================")
          println("Response Status: " + session("responseStatus").as[String])
          println("Response Body:")
          println(session("responseBody").as[String])
          println("========================================") // Returns the session so that Gatling can continue executing the scenario.
          session
        }

  // 3) Load Scenario
  // Starts the scenario with one user immediately.
  // The HTTP protocol configuration defined above is applied to the scenario.
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
