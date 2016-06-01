package com.github.yashap.model

import com.github.yashap.model.PaymentProcessor.PaymentProcessor
import play.api.libs.json._
import play.api.libs.functional.syntax._

import java.time.LocalDateTime

case class OrganizationPayment(
  id: String,
  timestamp: LocalDateTime,
  organizationId: Long,
  event: String,
  paymentAmount: Double,
  paymentProcessor: PaymentProcessor
)

object OrganizationPayment extends DateTimeFormats {
  def tupled = (OrganizationPayment.apply _).tupled

  implicit val userEventFormat: Format[OrganizationPayment] = (
    (JsPath \ "event_id").format[String] and
    (JsPath \ "timestamp").format[LocalDateTime] and
    (JsPath \ "organization_id").format[Long] and
    (JsPath \ "event").format[String] and
    (JsPath \ "extra_data" \ "payment_amount").format[Double] and
    (JsPath \ "extra_data" \ "payment_processor").format[PaymentProcessor]
  )(OrganizationPayment.apply, unlift(OrganizationPayment.unapply))
}