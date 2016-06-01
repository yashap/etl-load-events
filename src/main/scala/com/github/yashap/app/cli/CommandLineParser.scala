package com.github.yashap.app.cli

import java.time.{LocalDate, Instant, LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.Calendar

object CommandLineParser {
  private val zoneId: ZoneId = ZoneId.systemDefault()
  private[cli] def today = LocalDate.now(zoneId)
  private def todayStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  private val parser = new scopt.OptionParser[CommandLineArgs](s"<app run command>") {
    head("etl-organization-stats")

    opt[Calendar]('r', "runDate") action { (argVal, obj) =>
      obj.copy(runDate = calendarToLocalDate(argVal))
    } text s"runs app for this date (date, default $todayStr)"

    help("help") text "prints this usage text"
  }

  private def calendarToLocalDate(c: Calendar): LocalDate = {
    val instant = Instant.ofEpochMilli(c.getTimeInMillis)
    LocalDateTime.ofInstant(instant, zoneId).toLocalDate
  }

  def parse(args: Array[String]): CommandLineArgs = {
    parser.parse(args, CommandLineArgs()).getOrElse {
      throw new CommandLineArgParseException("Failed to parse command line args")
    }
  }
}
