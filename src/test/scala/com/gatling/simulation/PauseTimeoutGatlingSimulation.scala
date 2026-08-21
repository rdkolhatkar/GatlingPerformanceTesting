package com.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration.DurationInt

class PauseTimeoutGatlingSimulation extends Simulation {

  val httpProtocol = http.baseUrl("https://videogamedb.uk")
    .acceptHeader("application/json")

  val scn = scenario("VideoGame DB Test")

    .exec(
      http("Get All Games")
        .get("/api/videogame")
    )

    // pause(5): Pauses the virtual user's execution for exactly 5 seconds.
    // pause() is provided by Gatling Core DSL (io.gatling.core.Predef._).
    .pause(duration = 5)

    .exec(
      http("Get Specific Video Game")
        .get("/api/videogame/1")
    )

    // pause(1, 10): Pauses for a random duration between 1 and 10 seconds.
    // Useful for simulating realistic/random user behavior.
    .pause(1, 10)

    .exec(
      http("Get All Video Games")
        .get("/api/videogame")
    )

    // pause(3000.milliseconds): Pauses for exactly 3000 milliseconds (3 seconds).
    // DurationInt comes from Scala's duration API and provides the .milliseconds syntax.
    .pause(3000.milliseconds)

  // Other useful pause methods:
  //
  // .pause(5.seconds)
  // Pauses exactly 5 seconds. Requires scala.concurrent.duration.DurationInt.
  //
  // .pause(500.milliseconds)
  // Pauses exactly 500 milliseconds.
  //
  // .pause(1, 5)
  // Pauses for a random duration between 1 and 5 seconds.
  //
  // .pause(1.second, 5.seconds)
  // Pauses for a random duration between 1 second and 5 seconds.
  //
  // .pause(5.seconds).pause(2.seconds)
  // Adds two consecutive fixed pauses: 5 seconds followed by 2 seconds.
  //
  // .pause(
  //   1.second,
  //   5.seconds
  // )
  // Randomly pauses between the specified minimum and maximum durations.
  /*
    import java.time.Duration
    import scala.concurrent.duration.DurationInt

    // Pauses for exactly 5 seconds using Java's java.time.Duration.
    .pause(Duration.ofSeconds(5))

    // Pauses for exactly 3 seconds using Java's Duration in milliseconds.
    .pause(Duration.ofMillis(3000))
  */

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}