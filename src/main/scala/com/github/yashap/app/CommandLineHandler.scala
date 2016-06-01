package com.github.yashap.app

import com.github.yashap.app.cli.CommandLineParser
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Await}
import scala.util.{Failure, Success, Try}
import scala.language.reflectiveCalls

object CommandLineHandler extends App with ObjectGraph with LazyLogging {
  val config = ConfigFactory.load("dev")
  val cliArgs = CommandLineParser.parse(args)

  logger.info(s"Running Event Loading job with args $cliArgs")

  val jobRun: Future[Int] = pipeline
    .run(cliArgs.runDate)
    .andThen { case _ =>
      dataWarehouseConfig.db.close()
    }

  val recordsSaved: Try[Int] = Try(Await.result(jobRun, jobTimeout))

  recordsSaved match {
    case Success(n) =>
      logger.info(s"Event loading job completed successfully, $n records saved")
    case Failure(t) =>
      logger.error("Job failed", t)
  }

}
