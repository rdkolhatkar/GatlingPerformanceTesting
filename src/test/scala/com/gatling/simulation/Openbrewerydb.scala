package com.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps
class Openbrewerydb extends Simulation {

  // http protocol configuration
  val httpProtocol = http.baseUrl("https://api.openbrewerydb.org")

  // Scenario which will send the http request
  val scn = scenario("find_breweries")
    .exec(http("all_breweries").get("/breweries")).pause(5 seconds)
    .exec(http("get_single_brewery").get("/breweries/44"))

  // Inject Load
  setUp(scn.inject(atOnceUsers(2))).protocols(httpProtocol)


}
