package com.gatling.simulation.utils

import java.sql.{Connection, PreparedStatement, ResultSet}
import scala.collection.mutable.ArrayBuffer
import scala.util.Using

object DatabaseHelper {

  private val tableName = "userdatafeeder"

  def initializeDatabase(): Unit = {
    Using.resource(DatabaseConfig.getConnection()) { connection =>
      createTableIfNotExists(connection)
    }
  }

  private def createTableIfNotExists(connection: Connection): Unit = {
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
    }
  }

  def countUsers(): Long = {
    Using.resource(DatabaseConfig.getConnection()) { connection =>
      val sql = s"SELECT COUNT(*) FROM $tableName"

      Using.resource(connection.prepareStatement(sql)) { statement =>
        Using.resource(statement.executeQuery()) { resultSet =>
          resultSet.next()
          resultSet.getLong(1)
        }
      }
    }
  }

  def insertUsers(rows: Seq[Map[String, String]]): Unit = {
    if (rows.isEmpty) return

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
          statement.executeBatch()
        }

        connection.commit()
      } catch {
        case exception: Exception =>
          connection.rollback()
          throw exception
      } finally {
        connection.setAutoCommit(true)
      }
    }
  }

  def fetchUsers(): Seq[Map[String, Any]] = {
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
          val records = ArrayBuffer.empty[Map[String, Any]]

          while (resultSet.next()) {
            records += Map(
              "user_id" -> resultSet.getLong("user_id"),
              "user_name" -> resultSet.getString("user_name"),
              "email" -> resultSet.getString("email"),
              "first_name" -> resultSet.getString("first_name"),
              "last_name" -> resultSet.getString("last_name"),
              "mobile" -> resultSet.getString("mobile"),
              "password" -> resultSet.getString("password")
            )
          }

          records.toSeq
        }
      }
    }
  }
}
