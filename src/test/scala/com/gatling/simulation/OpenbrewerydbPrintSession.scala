package com.gatling.simulation

//import
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps
class OpenbrewerydbPrintSession  extends Simulation  {

  //Create http protocol configuration
  val httpProtocol=http.baseUrl("https://api.openbrewerydb.org")
  val csvFeeder=csv("data/OpenbreweryData.csv").eager.circular

  //Create Scenario which will send http request
  val scn = scenario("find_breweries")
    .feed(csvFeeder)
    .exec(http("all_breweries").get("/breweries").check(status.is(200),
      substring("Alabama").exists)).exitHereIfFailed
    .pause(5 seconds)
    .exec(http("get_single_brewery").get("/breweries/${brewery_id}").check(status.is(200)
      ,responseTimeInMillis.lte(1600)))
    .exec{session=>
      print(session)
      session
    }

  //Inject Load
  setUp(scn.inject(atOnceUsers(2))).protocols(httpProtocol)

}
