package com.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class GatlingCsvFeederSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://videogamedb.uk")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  // CSV Feeders
  val credentialsFeeder = csv("data/credentials.csv").circular
  val gamesFeeder = csv("data/games.csv").circular


  def authenticateToken() = {

    feed(credentialsFeeder)

      .exec(
        http("Generate Token for Authentication")
          .post("/api/authenticate")
          .header("Content-Type", "application/json")
          .header("Accept", "application/json")
          .body(
            StringBody(
              """{
              "password": "#{userPassword}",
              "username": "#{userName}"
            }"""
            )
          )
          .check(
            status.is(200),
            jsonPath("$.token").saveAs("jwtAuthToken")
          )
      )

      .exec { session =>

        println("======================================")
        println("USERNAME:")
        println(session("userName").as[String])

        println("PASSWORD:")
        println(session("userPassword").as[String])

        println("JWT TOKEN:")
        println(session("jwtAuthToken").as[String])

        println("======================================")

        session
      }
  }


  def createNewGame() = {

    feed(gamesFeeder)

      .exec(
        http("Create New Game")
          .post("/api/videogame")

          .header(
            "Authorization",
            "Bearer #{jwtAuthToken}"
          )

          .body(
            StringBody(
              """{
                "category": "Platform",
                "name": "#{gameName}",
                "rating": "Mature",
                "releaseDate": "2012-05-04",
                "reviewScore": 85
              }"""
            )
          )

          .check(
            bodyString.saveAs("createGameResponse")
          )
      )

      .exec { session =>

        println("======================================")
        println("GAME ID:")
        println(session("gameId").as[String])

        println("GAME NAME:")
        println(session("gameName").as[String])

        println("CREATE GAME API RESPONSE:")
        println(session("createGameResponse").as[String])

        println("======================================")

        session
      }
  }


  val scn = scenario("Authentication and Create Game")

    // Get username and password from credentials.csv
    .exec(authenticateToken())

    // Get game details from games.csv
    .exec(createNewGame())


  setUp(
    scn.inject(
      atOnceUsers(1)
    )
  ).protocols(httpProtocol)
}