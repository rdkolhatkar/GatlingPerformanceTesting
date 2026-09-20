package com.gatling.simulation.utils

import java.sql.{Connection, DriverManager}

object DatabaseConfig {

  private val host = sys.env.getOrElse("DB_HOST", "localhost")
  private val port = sys.env.getOrElse("DB_PORT", "3306")
  private val database = sys.env.getOrElse("DB_NAME", "gatlingdatabasefeeder")
  private val username = sys.env.getOrElse("DB_USERNAME", "root")

  // Do not commit the DB password to source control.
  // Windows PowerShell example:
  //   $env:DB_PASSWORD="your-password"
  private val password = sys.env.getOrElse("DB_PASSWORD", "Ratanlord@1409")

  val jdbcUrl =
    s"jdbc:mysql://$host:$port/$database" +
      "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"

  def getConnection(): Connection = {
    Class.forName("com.mysql.cj.jdbc.Driver")
    DriverManager.getConnection(jdbcUrl, username, password)
  }
}
