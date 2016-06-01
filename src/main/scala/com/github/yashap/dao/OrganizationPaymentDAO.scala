package com.github.yashap.dao

import com.github.yashap.model.OrganizationPayment
import com.github.yashap.model.PaymentProcessor.PaymentProcessor
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{Future, ExecutionContext}
import scala.util.Success
import java.time.LocalDateTime

class OrganizationPaymentDAO(val dbConfig: DatabaseConfig[JdbcProfile]) extends BaseDAO {
  import driver.api._

  val tableName = "organization_payment"

  private class OrganizationPaymentTable(tag: Tag) extends Table[OrganizationPayment](tag, tableName) {
    def id = column[String]("id", O.PrimaryKey)
    def timestamp = column[LocalDateTime]("timestamp")
    def organizationId = column[Long]("organization_id")
    def event = column[String]("event")
    def paymentAmount = column[Double]("payment_amount")
    def paymentProcessor = column[PaymentProcessor]("payment_processor")

    def * = (
      id, timestamp, organizationId, event, paymentAmount, paymentProcessor
    ) <> (OrganizationPayment.tupled, OrganizationPayment.unapply)
  }

  private val organizationPaymentsTQ = TableQuery[OrganizationPaymentTable]

  def upsert(organizationPayments: Seq[OrganizationPayment])(implicit ec: ExecutionContext): Future[Int] = {
    val action = DBIO.seq(organizationPayments.map(organizationPaymentsTQ.insertOrUpdate): _*)
    db.run(action)
      .map(_ => organizationPayments.length)
      .andThen { case Success(n) =>
        logger.debug(s"Saved $n organization payments")
      }
  }
}
