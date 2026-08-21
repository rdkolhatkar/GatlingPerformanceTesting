package com.gatling.simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ResponseStatusAndBodyCheckSimulation extends Simulation {

  val httpProtocol = http.baseUrl("https://videogamedb.uk")
    .acceptHeader("application/json")

  val scn = scenario("VideoGame DB Test")
    .exec(
      http("Get First Game")
        .get("/api/videogame/1")
        .check(status.is(200))
        .check(bodyString.saveAs("firstGameResponse"))
    )
    .exec { session =>
      println("========== First Game Response ==========")
      println(session("firstGameResponse").as[String])
      println("=========================================")
      session
    }
    .pause(5)
    .exec(
      http("Get Second Game")
        .get("/api/videogame/2")
        .check(status.in(200 to 210))
        .check(bodyString.saveAs("secondGameResponse"))
    )
    .exec { session =>
      println("========== Second Game Response ==========")
      println(session("secondGameResponse").as[String])
      println("==========================================")
      session
    }
    .pause(5)
    .exec(
      http("Get Third Game")
        .get("/api/videogame/3")
        .check(status.not(500))
        .check(bodyString.saveAs("thirdGameResponse"))
    )
    .exec { session =>
      println("========== Third Game Response ==========")
      println(session("thirdGameResponse").as[String])
      println("=========================================")
      session
    }

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}