package com.gatling.simulation.tests

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class BasicLoadSimulation extends Simulation {

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
        nothingFor(5),


        // -----------------------------------------------------
        // atOnceUsers(5)
        // -----------------------------------------------------
        // At the 5-second mark, 5 virtual users are started
        // IMMEDIATELY.
        //
        // Example:
        //
        // Time = 5 sec
        //
        //     User 1 ---> starts
        //     User 2 ---> starts
        //     User 3 ---> starts
        //     User 4 ---> starts
        //     User 5 ---> starts
        //
        // All 5 users start at approximately the same time.
        //
        atOnceUsers(5),


        // -----------------------------------------------------
        // rampUsers(10).during(10)
        // -----------------------------------------------------
        // After the atOnceUsers(5) injection,
        // Gatling starts another 10 virtual users gradually
        // over 10 seconds.
        //
        // Approximate rate:
        //
        // 10 users / 10 seconds = 1 user per second
        //
        // Timeline:
        //
        // 5 sec  -> 5 users already started
        // 6 sec  -> +1 user
        // 7 sec  -> +1 user
        // 8 sec  -> +1 user
        // ...
        // 15 sec -> +1 user
        //
        // Therefore, approximately 10 additional users are
        // started during these 10 seconds.
        //
        // IMPORTANT:
        // These are NEW users. They are in addition to the
        // 5 users created by atOnceUsers(5).
        //
        rampUsers(10).during(10)

      )

      // Apply the HTTP configuration to the scenario
      .protocols(httpProtocol)
  )
}