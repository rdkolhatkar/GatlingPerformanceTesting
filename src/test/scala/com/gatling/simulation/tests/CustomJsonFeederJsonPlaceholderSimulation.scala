package com.gatling.simulation.tests

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.util.Random

class CustomJsonFeederJsonPlaceholderSimulation extends Simulation {

  // ============================================================
  // HTTP CONFIGURATION
  // ============================================================

  val httpProtocol = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")


  // ============================================================
  // RANDOM DATA GENERATOR
  // ============================================================

  val randomGenerator = new Random()


  def randomString(length: Int): String = {

    randomGenerator.alphanumeric
      .filter(_.isLower)
      .take(length)
      .mkString

  }


  // ============================================================
  // CUSTOM JSON FEEDER
  // ============================================================

  val customFeeder = Iterator.continually(
    Map(
      "title" -> ("Post-" + randomString(5)),
      "body" -> ("This is a test post-" + randomString(10)),
      "userId" -> (randomGenerator.nextInt(10) + 1)
    )
  )


  // ============================================================
  // CREATE NEW POST
  // ============================================================

  def createNewPost(): ChainBuilder = {

    feed(customFeeder)

      .exec(

        http("Create New Post - #{title}")

          .post("/posts")

          .header("accept", "application/json")
          .header("Content-Type", "application/json")

          .body(
            StringBody(
              """{
                "title": "#{title}",
                "body": "#{body}",
                "userId": #{userId}
              }"""
            )
          )

          .check(

            // Verify API returned 201 Created
            status.is(201),

            // Save complete API response
            bodyString.saveAs("responseBody"),

            // Extract generated post ID
            jsonPath("$.id")
              .optional
              .saveAs("createdPostId")

          )
      )


      // ========================================================
      // PRINT API RESPONSE
      // ========================================================

      .exec { session =>

        println()
        println("====================================================")
        println("CREATE NEW POST")
        println("====================================================")

        println("TITLE:")
        println(session("title").as[String])

        println()

        println("BODY:")
        println(session("body").as[String])

        println()

        println("USER ID:")
        println(session("userId").as[Int])

        println()

        println("API RESPONSE:")
        println(session("responseBody").as[String])

        println()

        if (session.contains("createdPostId")) {

          println("CREATED POST ID:")
          println(session("createdPostId").as[String])

        }

        println("====================================================")
        println()

        session
      }
  }


  // ============================================================
  // SCENARIO
  // ============================================================

  val scn = scenario("JSONPlaceholder Custom JSON Feeder")

    .exec(createNewPost())


  // ============================================================
  // LOAD INJECTION
  // ============================================================

  setUp(

    scn.inject(

      // Start 1 virtual user immediately
      atOnceUsers(1)

    )

  ).protocols(httpProtocol)

}

