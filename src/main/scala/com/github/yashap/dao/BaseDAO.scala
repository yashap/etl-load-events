package com.github.yashap.dao

import com.github.yashap.model.PaymentProcessor._
import com.github.yashap.model.{PaymentProcessor, SocialNetworkType}
import com.github.yashap.model.SocialNetworkType._
import com.typesafe.scalalogging.LazyLogging
import slick.driver.JdbcProfile

import java.sql.Timestamp
import java.time.{ZoneOffset, LocalDateTime}

trait BaseDAO extends HasDatabaseConfig[JdbcProfile] with LazyLogging {
  import driver.api._

  val tableName: String

  protected implicit val localDateTimeColumnType = MappedColumnType.base[LocalDateTime, Timestamp](
    d => Timestamp.from(d.toInstant(ZoneOffset.ofHours(0))),
    d => d.toLocalDateTime
  )

  protected implicit val socialNetworkTypeColumnType = MappedColumnType.base[SocialNetworkType, String](
    e => e.toString,
    s => SocialNetworkType.withName(s)
  )

  protected implicit val paymentProcessorColumnType = MappedColumnType.base[PaymentProcessor, String](
    e => e.toString,
    s => PaymentProcessor.withName(s)
  )
}