package com.github.yashap.dao

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.typesafe.scalalogging.LazyLogging

import scala.collection.JavaConversions._
import scala.concurrent.{ExecutionContext, Future}
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.util.Success

class S3Client(val bucket: String, credentialsProvider: AWSCredentialsProvider) extends LazyLogging {
  import S3Client._

  private[dao] val s3Client = new AmazonS3Client(credentialsProvider)

  def getFile(s3Key: S3Key, filePrefix: String = "s3File", fileSuffix: String = ".txt")
    (implicit ec: ExecutionContext): Future[File] = {

    logger.debug(s"Getting file from s3://$bucket/$s3Key")

    val tempFile = File.createTempFile(filePrefix, fileSuffix)
    tempFile.deleteOnExit()
    val getObjectRequest = new GetObjectRequest(bucket, s3Key)

    Future(s3Client.getObject(getObjectRequest, tempFile))
      .map(_ => tempFile)
      .andThen { case Success(f) =>
          logger.debug(s"Transferred file from s3://$bucket/$s3Key to ${f.getCanonicalPath}")
      }
  }

  def getFiles(prefix: S3Prefix, filePrefix: String = "s3File", fileSuffix: String = ".txt")
    (implicit ec: ExecutionContext): Future[List[File]] = {

    listKeys(prefix).flatMap { s3Keys =>
      val files: List[Future[File]] = s3Keys.map(s3Key => getFile(s3Key, filePrefix, fileSuffix))
      Future.sequence(files)
    }
  }

  def listKeys(prefix: S3Prefix)(implicit ec: ExecutionContext): Future[List[S3Key]] = {
    Future(s3Client.listObjects(bucket, prefix).getObjectSummaries.toList.map(_.getKey))
  }
}

object S3Client {
  type S3Key = String
  type S3Prefix = String

  def dateToPartialKey(d: LocalDate, formatPattern: String = "yyyy/MM/dd"): String = {
    d.format(DateTimeFormatter.ofPattern(formatPattern))
  }
}
