package com.gatling.simulation.tests

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class GatlingCsvFeederJwtApiSimulation extends Simulation {

  // ============================================================
  // HTTP PROTOCOL
  // ============================================================

  val httpProtocol = http
    .baseUrl("https://api.qaautomationlabs.com/v1")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")


  // ============================================================
  // CSV FEEDERS
  // ============================================================

  val credentialsFeeder =
    csv("data/userdata.csv").circular

  val productsFeeder =
    csv("data/products.csv").circular


  // ============================================================
  // AUTHENTICATION
  // ============================================================

  def authenticateToken() = {

    feed(credentialsFeeder)

      .exec(
        http("Generate JWT Token")

          .post("/auth/login")

          .header("accept", "application/json")
          .header("Content-Type", "application/json")

          .body(
            StringBody(
              """{
                "email": "#{userEmail}",
                "password": "#{userPassword}"
              }"""
            )
          )

          .check(
            // Save HTTP status
            status.saveAs("loginStatus"),

            // Save complete API response
            bodyString.saveAs("loginResponse"),

            // Extract JWT token
            jsonPath("$.data.accessToken")
              .saveAs("jwtAuthToken")
          )
      )

      .exec { session =>

        println()
        println("======================================")
        println("LOGIN API")
        println("======================================")

        println("EMAIL:")
        println(
          session("userEmail").asOption[String].getOrElse("NOT FOUND")
        )

        println("PASSWORD:")
        println(
          session("userPassword").asOption[String].getOrElse("NOT FOUND")
        )

        println("LOGIN HTTP STATUS:")
        println(
          session("loginStatus").asOption[Int].getOrElse(-1)
        )

        println("LOGIN API RESPONSE:")
        println(
          session("loginResponse").asOption[String]
            .getOrElse("NO RESPONSE FOUND")
        )

        println("JWT TOKEN:")
        println(
          session("jwtAuthToken").asOption[String]
            .getOrElse("JWT TOKEN NOT FOUND")
        )

        println("======================================")
        println()

        session
      }
  }


  // ============================================================
  // CREATE PRODUCT
  // ============================================================

  def createNewProduct() = {

    feed(productsFeeder)

      .exec(
        http("Create New Product")

          .post("/products")

          .header("accept", "application/json")
          .header("Content-Type", "application/json")

          .header(
            "Authorization",
            "Bearer #{jwtAuthToken}"
          )

          .body(
            StringBody(
              """{
                "name": "#{productName}",
                "price": #{productPrice},
                "stock": #{productStock}
              }"""
            )
          )

          .check(

            // Save HTTP status
            status.saveAs("createProductStatus"),

            // Save complete response
            bodyString.saveAs("createProductResponse")
          )
      )

      .exec { session =>

        println()
        println("======================================")
        println("CREATE PRODUCT API")
        println("======================================")

        println("PRODUCT NAME:")
        println(
          session("productName").asOption[String]
            .getOrElse("NOT FOUND")
        )

        println("PRODUCT PRICE:")
        println(
          session("productPrice").asOption[String]
            .getOrElse("NOT FOUND")
        )

        println("PRODUCT STOCK:")
        println(
          session("productStock").asOption[String]
            .getOrElse("NOT FOUND")
        )

        println("PRODUCT API HTTP STATUS:")
        println(
          session("createProductStatus").asOption[Int]
            .getOrElse(-1)
        )

        println("CREATE PRODUCT API RESPONSE:")
        println(
          session("createProductResponse").asOption[String]
            .getOrElse("NO RESPONSE FOUND")
        )

        println("======================================")
        println()

        session
      }
  }


  // ============================================================
  // SCENARIO
  // ============================================================

  val scn =
    scenario("Authentication and Create Product")

      // ----------------------------------------------------------
      // STEP 1: Read credentials and generate JWT
      // ----------------------------------------------------------
      .exec(authenticateToken())

      // ----------------------------------------------------------
      // STEP 2: Read product data and create product
      // ----------------------------------------------------------
      .exec(createNewProduct())


  // ============================================================
  // SETUP
  // ============================================================

  setUp(
    scn.inject(
      atOnceUsers(1)
    )
  ).protocols(httpProtocol)

}
