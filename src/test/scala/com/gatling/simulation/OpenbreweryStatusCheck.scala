package com.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps
class OpenbreweryStatusCheck extends Simulation {


  val httpProtocol  =http.baseUrl("https://api.openbrewerydb.org")

  val scn = scenario("find_breweries")
    .exec(http("all_breweries").get("/breweries").check(status.is(200),
      substring("Alabama").exists))
    .pause(5 seconds)
    .exec(http("get_single_brewery").get("/breweries/44").check(status.is(200)
      ,responseTimeInMillis.lte(1600)))

  setUp(scn.inject(atOnceUsers(2))).protocols(httpProtocol)

}
