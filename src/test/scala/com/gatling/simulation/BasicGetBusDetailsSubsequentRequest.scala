package com.gatling.simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps
class BasicGetBusDetailsSubsequentRequest extends Simulation{
  val httpConf = http.baseUrl("http://developer.goibibo.com")

  val appId = "701491b9"
  val appKey = "a33c1f63da644e3ce7ba565599809d0f"
  val dateofDepart = "20190526"

  val csvfeeder_bus = csv("data/BusDetails.csv").circular

  val scn = scenario("GetBusDetails").feed(csvfeeder_bus).repeat(2) {
    pace(2 seconds)
      .exec(http("getbusrequest").get("/api/bus/search/?app_id=" + appId + "&app_key=" + appKey + "&format=json&source=${source}&destination=${destination}&dateofdeparture=" + dateofDepart)
        .check(jsonPath("$.data..skey").find.saveAs("searchKey")))
      .exec(http("getseatlayout").get("/api/bus/seatmap/?app_id=" + appId + "&app_key=" + appKey + "&format=json&skey=C 98c/api/bus/seatmap/?app_id=701491b9&app_key=a33c1f63da644e3ce7ba565599809d0f&format=json&skey=${searchKey}").check(bodyString.saveAs("myresponse")))
      .exec(session => {
        println(session("myresponse").as[String])
        session
      })
  }


  setUp(scn.inject(atOnceUsers(1))).protocols(httpConf)

}
