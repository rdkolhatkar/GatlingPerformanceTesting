package com.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class AuthenticatingInGatlingSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://videogamedb.uk")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")


  // =========================================================
  // 1. Authenticate and Generate JWT Token
  // =========================================================

  def authenticateToken() = {
    exec(
      http("Generate Token for Authentication")
        .post("/api/authenticate")
        .body(
          StringBody(
            """{
              "password": "admin",
              "username": "admin"
            }"""
          )
        )
        .check(
          // Extract JWT token from the response
          jsonPath("$.token").saveAs("jwtAuthToken")
        )
    )

      // Print the JWT token stored in the Gatling Session
      .exec { session =>

        println("======================================")
        println("JWT TOKEN:")
        println(session("jwtAuthToken").as[String])
        println("======================================")

        session
      }
  }


  // =========================================================
  // 2. Create New Game
  // =========================================================

  def createNewGame() = {

    exec(
      http("Create New Game")
        .post("/api/videogame")

        // Use JWT token from Gatling Session
        .header("Authorization", "Bearer #{jwtAuthToken}")

        .body(
          StringBody(
            """{
              "category": "Platform",
              "name": "Mario",
              "rating": "Mature",
              "releaseDate": "2012-05-04",
              "reviewScore": 85
            }"""
          )
        )

        // Check and save complete response body
        .check(
          bodyString.saveAs("createGameResponse")
        )
    )

      // Print API response
      .exec { session =>

        println("======================================")
        println("CREATE GAME API RESPONSE:")
        println(session("createGameResponse").as[String])
        println("======================================")

        session
      }
  }


  // =========================================================
  // Scenario
  // =========================================================

  val scn = scenario("Authentication API")

    // Generate JWT token
    .exec(authenticateToken())

    // Create game using JWT token
    .exec(createNewGame())


  // =========================================================
  // Setup
  // =========================================================

  setUp(
    scn.inject(
      atOnceUsers(1)
    )
  ).protocols(httpProtocol)
}