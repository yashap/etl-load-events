package com.github.yashap.app

import com.github.yashap.config._
import com.github.yashap.dao._
import com.github.yashap.pipeline.Pipeline
import com.typesafe.config.Config
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

trait ObjectGraph {

  val config: Config
  lazy val jobTimeout = config.getFiniteDuration("job.timeout")

  // DB DAOs
  lazy val dataWarehouseConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("dataWarehouse", config)
  lazy val userEventDAO = new UserEventDAO(dataWarehouseConfig)
  lazy val organizationPaymentDAO = new OrganizationPaymentDAO(dataWarehouseConfig)
  lazy val organizationEventDAO = new OrganizationEventDAO(dataWarehouseConfig)
  lazy val unknownEventDAO = new UnknownEventDAO(dataWarehouseConfig)

  // S3 client
  lazy val s3Config = config.getConfig("s3")
  lazy val s3Bucket = s3Config.getString("bucket")
  lazy val s3Prefix = s3Config.getString("prefix")
  lazy val s3Id = s3Config.getString("accessKeyId")
  lazy val s3Secret = s3Config.getString("secretAccessKey")

  lazy val s3CredentialsProvider = new BasicAWSCredentialsProvider(s3Id, s3Secret)
  lazy val s3Client = new S3Client(s3Bucket, s3CredentialsProvider)

  // Pipeline
  lazy val pipeline = new Pipeline(
    s3Client,
    s3Prefix,
    userEventDAO,
    organizationPaymentDAO,
    organizationEventDAO,
    unknownEventDAO
  )

}
