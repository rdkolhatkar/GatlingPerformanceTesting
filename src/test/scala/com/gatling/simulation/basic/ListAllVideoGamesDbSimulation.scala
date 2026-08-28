package com.gatling.simulation.basic

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ListAllVideoGamesDbSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://videogamedb.uk:443")
    .acceptHeader("application/json")

  val scn = scenario("VideoGame DB Test")

    // Get all games
    .exec(
      http("Get All Games")
        .get("/api/videogame")
        .check(
          status.is(200),
          bodyString.saveAs("AllGamesResponse"),
          jsonPath("$[5].id").saveAs("Game_ID")
        )
    )

    // Print Get All Games response
    .exec { session =>
      println("========================================")
      println("GET ALL GAMES - RESPONSE")
      println("========================================")
      println(session("AllGamesResponse").as[String])
      println("========================================")
      println("Extracted Game_ID = " + session("Game_ID").as[String])
      println("========================================")

      session
    }

    // Get specific game using extracted ID
    .exec(
      http("Get A Specific Game")
        .get("/api/videogame/#{Game_ID}")
        .check(
          status.is(200),
          bodyString.saveAs("SpecificGameResponse"),
          jsonPath("$.name").is("Doom")
        )
    )

    // Print Get Specific Game response
    .exec { session =>
      println("========================================")
      println("GET SPECIFIC GAME - RESPONSE")
      println("========================================")
      println(session("SpecificGameResponse").as[String])
      println("========================================")

      session
    }

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}