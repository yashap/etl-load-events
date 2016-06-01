package com.github.yashap.pipeline

import com.github.yashap.dao.S3Client.S3Prefix
import com.github.yashap.dao._
import com.github.yashap.model.{ExtractedEvents, Extractor}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Codec
import scala.io.Source
import java.io.File
import java.time.LocalDate

class Pipeline(
  s3Client: S3Client,
  s3Prefix: S3Prefix,
  userEventDAO: UserEventDAO,
  organizationPaymentDAO: OrganizationPaymentDAO,
  organizationEventDAO: OrganizationEventDAO,
  unknownEventDAO: UnknownEventDAO
) {
  implicit val codec: Codec = Codec.UTF8

  private[pipeline] def processFile(file: File)(implicit ec: ExecutionContext): Future[Int] = {
    val lines: List[String] = Source.fromFile(file).getLines().toList
    val jsonLines = lines.map(Json.parse)
    val events: ExtractedEvents = Extractor.extractEvents(jsonLines)

    val userEventResult: Future[Int] = userEventDAO.upsert(events.userEvents)
    val orgPmtsResult = organizationPaymentDAO.upsert(events.organizationPayments)
    val orgEventResult = organizationEventDAO.upsert(events.organizationEvents)
    val unknownEventResult = unknownEventDAO.upsert(events.unknownEvents)

    Future.sequence(Seq(
      userEventResult, orgPmtsResult, orgEventResult, unknownEventResult
    )).map(_.sum)
  }

  private[pipeline] def processFiles(files: List[File])(implicit ec: ExecutionContext): Future[Int] = {
    val fileLoad: List[Future[Int]] = files.map { file =>
      processFile(file).andThen { case _ => file.delete() }
    }

    Future.sequence(fileLoad).map(_.sum)
  }

  def run(endDate: LocalDate)(implicit ec: ExecutionContext): Future[Int] = {
    val s3PrefixForRun = s"$s3Prefix/${S3Client.dateToPartialKey(endDate)}"

    s3Client.getFiles(s3PrefixForRun).flatMap(processFiles)
  }
}
