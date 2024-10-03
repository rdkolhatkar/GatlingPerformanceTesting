package com.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
class BasicGetBusDetailsMyString extends Simulation {
  val httpConf = http.baseUrl("http://developer.goibibo.com")

  val appId="701491b9"
  val appKey="a33c1f63da644e3ce7ba565599809d0f"
  val dateofDepart="20190426"

  val csvfeeder_bus= csv("data/BusDetails.csv").circular

  val scn = scenario("GetBusDetails").feed(csvfeeder_bus)
    .exec(http("getbusrequest").get("/api/bus/search/?app_id="+ appId+"&app_key="+ appKey+"&format=json&source=${source}&destination=${destination}&dateofdeparture="+ dateofDepart)
      .check(jsonPath("$.data..skey").find.saveAs("searchKey")))
    .exec(http("getBusSeatLayout").get("/api/bus/seatmap/?app_id="+appId+"&app_key="+appKey+"&format=json&skey=${searchKey}").check(bodyString.saveAs("response2")))
    .exec(session => {
      val mystring2= session("response2").as[String]
      println(mystring2)
      session
    })
  setUp(scn.inject(atOnceUsers(1))).protocols(httpConf)
}
