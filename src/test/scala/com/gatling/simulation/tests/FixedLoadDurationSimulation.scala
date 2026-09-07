package com.gatling.simulation.tests

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class FixedLoadDurationSimulation extends Simulation {

  // =========================================================
  // HTTP CONFIGURATION
  // =========================================================
  //
  // This defines the common HTTP configuration for the scenario.
  //
  // baseUrl()
  // ---------------------------------------------------------
  // Instead of writing the complete URL in every request:
  //
  //     https://jsonplaceholder.typicode.com/posts
  //
  // we can simply write:
  //
  //     /posts
  //
  // Gatling will combine them:
  //
  //     baseUrl + endpoint
  //
  // =========================================================

  val httpProtocol = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")


  // =========================================================
  // GET ALL POSTS
  // =========================================================
  //
  // This method returns a ChainBuilder.
  //
  // The chain performs:
  //
  // 1. GET /posts
  // 2. Saves the response body into the Gatling Session
  // 3. Prints the response body to the console
  //
  // =========================================================

  def getAllJsonPosts(): ChainBuilder = {

    exec(
      http("Get All Json Posts")
        .get("/posts")

        // -----------------------------------------------------
        // Save the complete HTTP response body into Session.
        //
        // The response will be stored using the key:
        //
        //     allPostsResponse
        //
        // Later we can access it using:
        //
        //     session("allPostsResponse").as[String]
        //
        // -----------------------------------------------------

        .check(
          bodyString.saveAs("allPostsResponse")
        )
    )

      // -------------------------------------------------------
      // Execute Scala code after the HTTP request completes.
      //
      // The response body is retrieved from the Session and
      // printed to the console.
      // -------------------------------------------------------

      .exec { session =>

        println("==============================================")
        println("Response Body - GET /posts")
        println("==============================================")

        println(
          session("allPostsResponse").as[String]
        )

        println("==============================================")

        // IMPORTANT:
        // Always return the Session.
        //
        // Returning the Session allows Gatling to continue
        // executing the scenario.
        //
        session
      }
  }


  // =========================================================
  // GET SPECIFIC POST
  // =========================================================
  //
  // This method performs:
  //
  //     GET /posts/1
  //
  // The response is saved in:
  //
  //     specificPostResponse
  //
  // and then printed to the console.
  //
  // =========================================================

  def getSpecificJsonPost(): ChainBuilder = {

    exec(
      http("Get Specific Json Post")
        .get("/posts/1")

        // Save response body into Gatling Session
        .check(
          bodyString.saveAs("specificPostResponse")
        )
    )

      .exec { session =>

        println("==============================================")
        println("Response Body - GET /posts/1")
        println("==============================================")

        println(
          session("specificPostResponse").as[String]
        )

        println("==============================================")

        session
      }
  }


  // =========================================================
  // SCENARIO
  // =========================================================
  //
  // This defines the actual business flow executed by users.
  //
  // scenario()
  // ---------------------------------------------------------
  // Creates a Gatling scenario.
  //
  // forever { }
  // ---------------------------------------------------------
  // Means:
  //
  //     Keep executing the enclosed actions repeatedly.
  //
  // Without forever:
  //
  //     GET /posts
  //     pause 5 sec
  //     GET /posts/1
  //     pause 5 sec
  //     GET /posts
  //     END
  //
  // With forever:
  //
  //     GET /posts
  //     pause 5 sec
  //     GET /posts/1
  //     pause 5 sec
  //     GET /posts
  //
  //     GET /posts
  //     pause 5 sec
  //     GET /posts/1
  //     pause 5 sec
  //     GET /posts
  //
  //     GET /posts
  //     ...
  //
  // It keeps repeating until something stops the scenario.
  //
  // In THIS simulation, maxDuration(60) is what puts an
  // upper limit on the simulation.
  //
  // =========================================================

  val scn = scenario("Fixed duration Load Simulation")
    .forever {

      // -------------------------------------------------------
      // ITERATION STARTS
      // -------------------------------------------------------

      // 1. Send GET /posts
      exec(getAllJsonPosts())

        // -------------------------------------------------------
        // Wait for 5 seconds.
        //
        // pause() does NOT block the Gatling engine thread.
        // It represents user think-time/waiting time.
        // -------------------------------------------------------

        .pause(5)

        // -------------------------------------------------------
        // 2. Send GET /posts/1
        // -------------------------------------------------------

        .exec(getSpecificJsonPost())

        // -------------------------------------------------------
        // Wait another 5 seconds.
        // -------------------------------------------------------

        .pause(5)

        // -------------------------------------------------------
        // 3. Send GET /posts again
        // -------------------------------------------------------

        .exec(getAllJsonPosts())

      // -------------------------------------------------------
      // Because this entire block is inside forever{},
      // Gatling goes back to the beginning of this block.
      //
      // Therefore it starts again with:
      //
      //     GET /posts
      //
      // -------------------------------------------------------
    }


  // =========================================================
  // LOAD / INJECTION PROFILE
  // =========================================================
  //
  // This section defines HOW MANY USERS enter the scenario
  // and WHEN they enter.
  //
  // =========================================================

  setUp(
    scn.inject(

        // -------------------------------------------------------
        // STEP 1: Wait for 5 seconds
        //
        // No users are started during the first 5 seconds.
        // -------------------------------------------------------

        nothingFor(5),

        // -------------------------------------------------------
        // STEP 2: Start 10 users immediately
        //
        // At approximately 5 seconds:
        //
        //     10 users
        //
        // are injected into the scenario at once.
        // -------------------------------------------------------

        atOnceUsers(10),

        // -------------------------------------------------------
        // STEP 3: Ramp up another 5 users over 10 seconds.
        //
        // 5 additional users are gradually started during
        // a 10-second period.
        //
        // Approximately:
        //
        //     5 sec  -> 10 users
        //     15 sec -> 15 users
        //
        // The exact scheduling is handled by Gatling.
        // -------------------------------------------------------

        rampUsers(5).during(10)

      )

      // -------------------------------------------------------
      // Apply the HTTP configuration to this scenario.
      // -------------------------------------------------------

      .protocols(httpProtocol)

  )

    // =========================================================
    // MAXIMUM SIMULATION DURATION
    // =========================================================
    //
    // maxDuration(60)
    //
    // means:
    //
    //     The ENTIRE simulation is allowed to run for a
    //     maximum of 60 seconds.
    //
    // This is different from forever{}.
    //
    // forever{} says:
    //
    //     "Keep repeating the scenario."
    //
    // maxDuration(60) says:
    //
    //     "But don't let the simulation run longer than
    //      60 seconds."
    //
    // Therefore:
    //
    //     forever{} + maxDuration(60)
    //
    // means:
    //
    //     "Keep repeating the scenario, but stop the
    //      simulation after 60 seconds."
    //
    // =========================================================

    .maxDuration(60)
}