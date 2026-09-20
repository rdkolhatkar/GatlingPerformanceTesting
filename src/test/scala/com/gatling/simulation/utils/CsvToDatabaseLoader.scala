package com.gatling.simulation.utils

import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters._
import scala.util.Using

object CsvToDatabaseLoader {

  private val tableName = "userdatafeeder"

  def loadIfTableIsEmpty(csvResource: String): Unit = {
    val count = DatabaseHelper.countUsers()

    if (count == 0) {
      println(s"Table $tableName is empty. Loading CSV data into MySQL...")
      val rows = readCsv(csvResource)
      DatabaseHelper.insertUsers(rows)
      println(s"Inserted ${rows.size} records into $tableName.")
    } else {
      println(s"Table $tableName already contains $count record(s). CSV insert skipped.")
    }
  }

  private def readCsv(resourceName: String): Seq[Map[String, String]] = {
    val stream = Option(getClass.getClassLoader.getResourceAsStream(resourceName))
      .getOrElse(throw new IllegalArgumentException(
        s"CSV resource not found: $resourceName"
      ))

    Using.resource(scala.io.Source.fromInputStream(stream, "UTF-8")) { source =>
      val lines = source.getLines().filter(_.trim.nonEmpty).toList

      if (lines.isEmpty) {
        Seq.empty
      } else {
        val headers = lines.head.split(",", -1).map(_.trim).toSeq

        lines.tail.map { line =>
          val values = line.split(",", -1).map(_.trim)
          if (values.length != headers.length) {
            throw new IllegalArgumentException(
              s"Invalid CSV row. Expected ${headers.length} columns but found ${values.length}: $line"
            )
          }
          headers.zip(values).toMap
        }
      }
    }
  }
}
