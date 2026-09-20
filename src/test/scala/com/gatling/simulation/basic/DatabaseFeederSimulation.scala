
package com.gatling.simulation.basic

import com.gatling.simulation.utils.{
  CsvToDatabaseLoader,
  DatabaseHelper
}
import io.gatling.core.Predef._
import io.gatling.core.session.Session
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import scala.concurrent.duration._

class DatabaseFeederSimulation extends Simulation {

  // ---------------------------------------------------------
  // HTTP CONFIGURATION
  // ---------------------------------------------------------

  val httpProtocol = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  // ---------------------------------------------------------
  // DATABASE INITIALIZATION
  // ---------------------------------------------------------

  DatabaseHelper.initializeDatabase()

  CsvToDatabaseLoader.loadIfTableIsEmpty(
    "data/users.csv"
  )

  val databaseUsers: Seq[Map[String, Any]] =
    DatabaseHelper.fetchUsers()

  require(
    databaseUsers.nonEmpty,
    "No database records found. Please check users.csv and MySQL."
  )

  println("==============================================")
  println(s"Records fetched from MySQL: ${databaseUsers.size}")
  println("==============================================")

  databaseUsers.foreach { user =>
    println(
      s"user_id=${user("user_id")}, " +
        s"user_name=${user("user_name")}, " +
        s"email=${user("email")}"
    )
  }

  // ---------------------------------------------------------
  // GATLING FEEDER
  // ---------------------------------------------------------

  // Gatling feeder records must be Map[String, Any].
  // databaseUsers already has this structure.
  //
  // Convert the database records into a circular feeder.

  val databaseFeeder =
    Iterator.continually(databaseUsers).flatten

  // ---------------------------------------------------------
  // SCENARIO
  // ---------------------------------------------------------

  val scn: ScenarioBuilder =
    scenario("MySQL Database Feeder Simulation")

      .feed(databaseFeeder)

      // -------------------------------------------------------
      // GET /posts/${user_id}
      // -------------------------------------------------------

      .exec(
        http("Get Post Using DB user_id")
          .get("/posts/${user_id}")
          .check(status.is(200))
          .check(bodyString.saveAs("postResponse"))
      )

      // -------------------------------------------------------
      // PRINT DATABASE AND RESPONSE DATA
      // -------------------------------------------------------

      .exec { session: Session =>

        println("----------------------------------------------")

        println(
          s"user_id = ${session("user_id").as[Any]}"
        )

        println(
          s"user_name = ${session("user_name").as[String]}"
        )

        println(
          s"email = ${session("email").as[String]}"
        )

        println(
          s"first_name = ${session("first_name").as[String]}"
        )

        println(
          s"last_name = ${session("last_name").as[String]}"
        )

        println(
          s"mobile = ${session("mobile").as[String]}"
        )

        println("Response from JSONPlaceholder:")

        println(
          session("postResponse").as[String]
        )

        println("----------------------------------------------")

        session
      }

      // -------------------------------------------------------
      // PAUSE
      // -------------------------------------------------------

      .pause(2.seconds)

      // -------------------------------------------------------
      // GET ALL POSTS
      // -------------------------------------------------------

      .exec(
        http("Get All Posts")
          .get("/posts")
          .check(status.is(200))
      )

  // ---------------------------------------------------------
  // LOAD / INJECTION PROFILE
  // ---------------------------------------------------------

  setUp(
    scn.inject(
      nothingFor(5.seconds),

      constantUsersPerSec(1)
        .during(1.seconds),

//      rampUsersPerSec(1)
//        .to(5)
//        .during(20.seconds)
    )
  ).protocols(httpProtocol)
}