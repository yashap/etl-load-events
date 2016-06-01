package com.github.yashap.app.cli

import java.time.LocalDate

case class CommandLineArgs(runDate: LocalDate = CommandLineParser.today)
