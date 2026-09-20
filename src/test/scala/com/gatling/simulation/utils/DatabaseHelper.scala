
package com.gatling.simulation.utils

import java.sql.Connection
import scala.collection.mutable.ArrayBuffer
import scala.util.Using

object DatabaseHelper {

  private val tableName = "userdatafeeder"

  private def logInfo(message: String): Unit =
    println(s"[INFO] [DatabaseHelper] $message")

  private def logSuccess(message: String): Unit =
    println(s"[SUCCESS] [DatabaseHelper] $message")

  private def logError(message: String): Unit =
    println(s"[ERROR] [DatabaseHelper] $message")

  // --------------------------------------------------
  // Initialize database and create table if required
  // --------------------------------------------------
  def initializeDatabase(): Unit = {

    logInfo("Initializing database connection...")

    Using.resource(DatabaseConfig.getConnection()) { connection =>

      logSuccess("Database connection established.")

      createTableIfNotExists(connection)
    }
  }

  // --------------------------------------------------
  // Create table if it does not exist
  // --------------------------------------------------
  private def createTableIfNotExists(
                                      connection: Connection
                                    ): Unit = {

    val sql =
      s"""
         |CREATE TABLE IF NOT EXISTS $tableName (
         |    user_id BIGINT NOT NULL AUTO_INCREMENT,
         |    user_name VARCHAR(255) NOT NULL,
         |    email VARCHAR(255) NOT NULL,
         |    first_name VARCHAR(255) NOT NULL,
         |    last_name VARCHAR(255) NOT NULL,
         |    mobile VARCHAR(10) NOT NULL,
         |    password VARCHAR(255) NOT NULL,
         |    PRIMARY KEY (user_id),
         |    UNIQUE KEY uk_user_name (user_name)
         |)
         |""".stripMargin

    Using.resource(connection.createStatement()) { statement =>

      statement.executeUpdate(sql)

      logSuccess(
        s"Table '$tableName' is available (created if it did not exist)."
      )
    }
  }

  // --------------------------------------------------
  // Count records in database
  // --------------------------------------------------
  def countUsers(): Long = {

    logInfo(s"Checking record count in table '$tableName'...")

    val count =
      Using.resource(DatabaseConfig.getConnection()) { connection =>

        val sql = s"SELECT COUNT(*) FROM $tableName"

        Using.resource(connection.prepareStatement(sql)) { statement =>

          Using.resource(statement.executeQuery()) { resultSet =>

            resultSet.next()
            resultSet.getLong(1)
          }
        }
      }

    logInfo(s"Current record count: $count")

    count
  }

  // --------------------------------------------------
  // Insert CSV records into database
  // --------------------------------------------------
  def insertUsers(rows: Seq[Map[String, String]]): Unit = {

    if (rows.isEmpty) {
      logInfo("No CSV records received. Nothing to insert.")
      return
    }

    logInfo(
      s"Attempting to insert ${rows.size} records into '$tableName'..."
    )

    val sql =
      s"""
         |INSERT INTO $tableName
         |(user_name, email, first_name, last_name, mobile, password)
         |VALUES (?, ?, ?, ?, ?, ?)
         |""".stripMargin

    Using.resource(DatabaseConfig.getConnection()) { connection =>

      connection.setAutoCommit(false)

      try {

        Using.resource(connection.prepareStatement(sql)) { statement =>

          rows.foreach { row =>

            statement.setString(1, row("user_name"))
            statement.setString(2, row("email"))
            statement.setString(3, row("first_name"))
            statement.setString(4, row("last_name"))
            statement.setString(5, row("mobile"))
            statement.setString(6, row("password"))

            statement.addBatch()
          }

          val results = statement.executeBatch()

          logInfo(
            s"Batch executed. JDBC returned ${results.length} result entries."
          )
        }

        connection.commit()

        logSuccess(
          s"Transaction committed. Inserted ${rows.size} CSV records into '$tableName'."
        )

      } catch {

        case exception: Exception =>

          logError(
            s"Insert failed: ${exception.getClass.getSimpleName}: ${exception.getMessage}"
          )

          try {
            connection.rollback()
            logInfo("Transaction rolled back.")
          } catch {
            case rollbackException: Exception =>
              logError(
                s"Rollback failed: ${rollbackException.getMessage}"
              )
          }

          throw exception

      } finally {

        connection.setAutoCommit(true)
      }
    }

    // Verify the number of records after insertion
    val finalCount = countUsers()

    logInfo(
      s"Record count after insertion: $finalCount"
    )
  }

  // --------------------------------------------------
  // Fetch all records from database
  // --------------------------------------------------
  def fetchUsers(): Seq[Map[String, Any]] = {

    logInfo(s"Fetching records from '$tableName'...")

    val records =
      Using.resource(DatabaseConfig.getConnection()) { connection =>

        val sql =
          s"""
             |SELECT user_id, user_name, email, first_name,
             |       last_name, mobile, password
             |FROM $tableName
             |ORDER BY user_id
             |""".stripMargin

        Using.resource(connection.prepareStatement(sql)) { statement =>

          Using.resource(statement.executeQuery()) { resultSet =>

            val result = ArrayBuffer.empty[Map[String, Any]]

            while (resultSet.next()) {

              result += Map(
                "user_id" -> resultSet.getLong("user_id"),
                "user_name" -> resultSet.getString("user_name"),
                "email" -> resultSet.getString("email"),
                "first_name" -> resultSet.getString("first_name"),
                "last_name" -> resultSet.getString("last_name"),
                "mobile" -> resultSet.getString("mobile"),
                "password" -> resultSet.getString("password")
              )
            }

            result.toSeq
          }
        }
      }

    logInfo(s"Fetched ${records.size} records from '$tableName'.")

    records
  }
}