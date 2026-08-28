package com.gatling.simulation.basic

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Random
class ComplexCustomFeederSimulation extends Simulation{

  val httpProtocol = http
    .baseUrl("https://videogamedb.uk:443")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  var idNumbers = (1 to 10).iterator

  val randomGenerator = new Random()

  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  val now = LocalDate.now()
  def randomString(length: Int) = {
    randomGenerator.alphanumeric.filter(_.isLower).take(length).mkString
  }

  def randomDate(startedData: LocalDate, random: Random): String = {
    startedData.minusDays(random.nextInt(30)).format(pattern)
  }

  def authenticate(): ChainBuilder = {
    exec(
      http("Authenticate")
        .post("/api/authenticate")
        .body(StringBody("{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}"))
        .check(jsonPath("$.token").saveAs("jwtAuthToken"))
    )
  }

  val customFeeder = Iterator.continually(Map(
    "gameId" -> idNumbers.next(),
    "name" -> ("Game-" + randomString(5)),
    "releaseDate" -> randomDate(now, randomGenerator),
    "reviewScore" -> randomGenerator.nextInt(100),
    "category" -> ("Category-"+randomString(6)),
    "rating" -> ("Rating-"+randomString(4))
  ))


  def createNewGame(): ChainBuilder = {
    repeat(10){
      feed(customFeeder)
        .exec(http("Create New Game - #{name}")
        .post("/api/videogame")
        .header("authorization", "Bearer #{jwtAuthToken}")
          .body(StringBody(
            """{
              "id": #{gameId},
              "name": "#{name}",
              "releaseDate": "#{releaseDate}",
              "reviewScore": #{reviewScore},
              "category": "#{category}",
              "rating": "#{rating}"
            }"""))
          .check(bodyString.saveAs("responseBody"))
        )
        .exec{session => println(session("responseBody").as[String]); session}
        .pause(1)
    }
  }
  val scn = scenario("Complex Custom Feeders").exec(authenticate()).exec(createNewGame())
  setUp(
    scn.inject(
      atOnceUsers(1)
    )
  ).protocols(httpProtocol)

}
