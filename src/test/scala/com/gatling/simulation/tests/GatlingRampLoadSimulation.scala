package com.gatling.simulation.tests

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class GatlingRampLoadSimulation extends Simulation {

  // ---------------------------------------------------------
  // HTTP CONFIGURATION
  // ---------------------------------------------------------

  val httpProtocol = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")


  // ---------------------------------------------------------
  // GET ALL POSTS
  // ---------------------------------------------------------

  def getAllJsonPosts(): ChainBuilder = {

    exec(
      http("Get All Json Posts")
        .get("/posts")

        // Save the complete response body into the Gatling Session
        .check(
          bodyString.saveAs("allPostsResponse")
        )
    )

      // Print the response body stored in the Session
      .exec { session =>

        println("==============================================")
        println("Response Body - GET /posts")
        println("==============================================")
        println(session("allPostsResponse").as[String])
        println("==============================================")

        session
      }
  }


  // ---------------------------------------------------------
  // GET SPECIFIC POST
  // ---------------------------------------------------------

  def getSpecificJsonPost(): ChainBuilder = {

    exec(
      http("Get Specific Json Post")
        .get("/posts/1")

        // Save the response body
        .check(
          bodyString.saveAs("specificPostResponse")
        )
    )

      // Print the response body
      .exec { session =>

        println("==============================================")
        println("Response Body - GET /posts/1")
        println("==============================================")
        println(session("specificPostResponse").as[String])
        println("==============================================")

        session
      }
  }


  // ---------------------------------------------------------
  // SCENARIO
  // ---------------------------------------------------------

  val scn = scenario("Basic Load Simulation")

    // 1. GET /posts
    .exec(getAllJsonPosts())

    // Wait 5 seconds
    .pause(5)

    // 2. GET /posts/1
    .exec(getSpecificJsonPost())

    // Wait 5 seconds
    .pause(5)

    // 3. GET /posts again
    .exec(getAllJsonPosts())


  // ---------------------------------------------------------
  // LOAD / INJECTION PROFILE
  // ---------------------------------------------------------

  setUp(
    scn.inject(

        // -----------------------------------------------------
        // nothingFor(5)
        // -----------------------------------------------------
        // No virtual users are started for the first 5 seconds.
        //
        // Timeline:
        //
        // 0 sec -------------------- 5 sec
        //       NO USERS
        //
        // During these first 5 seconds, Gatling is running,
        // but no virtual user executes the scenario.
        //
        // This creates a 5-second "waiting period" before
        // the actual load test starts.
        //
        nothingFor(5),


        // -----------------------------------------------------
        // constantUsersPerSec(10).during(10)
        // -----------------------------------------------------
        // Gatling now starts users at a CONSTANT rate of
        // 10 users per second.
        //
        // Duration = 10 seconds
        //
        // Rate = 10 users / second
        //
        // Therefore:
        //
        // 10 users/sec × 10 sec = 100 users
        //
        // Approximately 100 NEW virtual users are started
        // during this injection step.
        //
        //
        // Timeline:
        //
        // Time = 5 sec
        //     ↓
        //     Load injection starts
        //
        // 5 sec  -> +10 users
        // 6 sec  -> +10 users
        // 7 sec  -> +10 users
        // 8 sec  -> +10 users
        // 9 sec  -> +10 users
        // 10 sec -> +10 users
        // 11 sec -> +10 users
        // 12 sec -> +10 users
        // 13 sec -> +10 users
        // 14 sec -> +10 users
        // 15 sec -> +10 users
        //
        // Total:
        //
        // 10 × 10 = 100 users
        //
        //
        // IMPORTANT:
        // constantUsersPerSec() controls the RATE at which
        // NEW virtual users are started.
        //
        // It does NOT mean that only 10 users exist.
        //
        // At the end of this injection step, approximately
        // 100 users have been started.
        //
        constantUsersPerSec(10).during(10),


        // -----------------------------------------------------
        // rampUsersPerSec(1).to(5).during(20)
        // -----------------------------------------------------
        // Gatling now changes the user injection RATE
        // gradually from 1 user/second to 5 users/second.
        //
        // IMPORTANT:
        //
        // This is NOT the same as:
        //
        // rampUsers(5).during(20)
        //
        // Here we are ramping the RATE of user creation,
        // not simply creating 5 users.
        //
        //
        // STARTING RATE:
        //
        // 1 user per second
        //
        // ENDING RATE:
        //
        // 5 users per second
        //
        // DURATION:
        //
        // 20 seconds
        //
        //
        // The injection rate increases gradually during the
        // 20-second period.
        //
        //
        // Timeline:
        //
        // At the beginning:
        //
        // 1 user/sec
        //
        //        ↓
        //
        //        Rate gradually increases
        //
        //        ↓
        //
        // 5 users/sec
        //
        //
        // Simplified visualization:
        //
        // 15 sec  -> 1 user/sec
        // 20 sec  -> approximately 2 users/sec
        // 25 sec  -> approximately 3 users/sec
        // 30 sec  -> approximately 4 users/sec
        // 35 sec  -> 5 users/sec
        //
        // The exact user-start timing is controlled by Gatling's
        // injection scheduler, so the above is an easy-to-understand
        // approximation rather than an exact per-second schedule.
        //
        //
        // IMPORTANT:
        //
        // The users created by this step are NEW users.
        //
        // They are NOT the same users that were started during
        // constantUsersPerSec(10).
        //
        // Therefore, the overall test has:
        //
        // 1. First 5 seconds:
        //       0 users started
        //
        // 2. Next 10 seconds:
        //       100 users started
        //
        // 3. Next 20 seconds:
        //       Additional users started at an increasing rate
        //       from 1 user/sec → 5 users/sec
        //
        // -----------------------------------------------------
        //
        // Approximate number of users generated by this
        // ramping rate:
        //
        // Average rate = (1 + 5) / 2
        //              = 3 users/sec
        //
        // Duration = 20 seconds
        //
        // Approximate users =
        //
        // 3 × 20
        // = 60 users
        //
        // Therefore, approximately 60 additional users are
        // started during this injection step.
        //
        // Combined with the previous 100 users:
        //
        // 100 + approximately 60
        // = approximately 160 users started in total.
        //
        // NOTE:
        // "users started" does NOT necessarily mean
        // "160 users are simultaneously active".
        //
        // Some users may have already completed the scenario
        // before later users are started.
        //
        rampUsersPerSec(1).to(5).during(20)
      )

      // Apply the HTTP configuration to the scenario
      .protocols(httpProtocol)
  )
}