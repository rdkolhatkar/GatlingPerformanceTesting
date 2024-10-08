package com.gatling.simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps
class BasicGetScript extends Simulation{
  val httpConf = http.baseUrl("http://newtours.demoaut.com")

  val scn = scenario("basicgetscenario").repeat(4) {
    pace(2 seconds)
      .exec(http("basicgetrequest").get("/mercurycruise.php"))
  }

  setUp(scn.inject(rampUsers(5) during (10 seconds))).protocols(httpConf)
}
