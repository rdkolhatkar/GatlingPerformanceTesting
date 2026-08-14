package com.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class GetVideoGameApiSimulation extends Simulation{

  val httpProtocol = http.baseUrl("https://videogamedb.uk")
    .acceptHeader("application/json")

  val scn = scenario("Check Video Game Response Body")
    .exec(
      http("Get First Game")
        .get("/api/videogame/1")
        .check(status.is(200))
        // Check Video Game Response Body contains a specific element or noy
        .check(jsonPath("x.rating").is("Universal"))
        .check(bodyString.saveAs("firstGameResponse"))
    )
    .exec { session =>
      println("========== First Game Response ==========")
      println(session("firstGameResponse").as[String])
      println("=========================================")
      session
    }

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
