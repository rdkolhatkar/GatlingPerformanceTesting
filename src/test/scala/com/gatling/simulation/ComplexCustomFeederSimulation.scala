package com.gatling.simulation
import io.gatling.core.Predef._
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

  val randomString = new Random()

  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  val now = LocalDate.now()
  def randomString(length: Int) = {
    randomString.alphanumeric.filter(_.isLower).take(length).mkString
  }

  def randomDate(startedData: LocalDate, random: Random): String = {
    startedData.minusDays(random.nextInt(30)).format(pattern)
  }

  val scn = scenario("Complex Custom Feeders")

  val customFeeder = Iterator.continually(Map(
    "gameId" -> idNumbers.next(),
    "name" -> ("Game-" + randomString(5)),
    "releaseDate" -> randomDate(now, randomString),
    "reviewScore" -> randomString.nextInt(100),
    "category" -> ("Category-"+randomString(6)),
    "rating" -> ("Rating-"+randomString(4))
  ))

  setUp(
    scn.inject(
      atOnceUsers(1)
    )
  ).protocols(httpProtocol)

}
