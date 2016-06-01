package com.github.yashap.model

import play.api.data.validation.ValidationError
import play.api.libs.json._

import scala.util.Try

object PaymentProcessor extends Enumeration {
  type PaymentProcessor = Value

  val Chase = Value("Chase")
  val PayPal = Value("PayPal")

  implicit val paymentProcessorFormat = new Format[PaymentProcessor] {
    def writes(sn: PaymentProcessor): JsValue = JsString(sn.toString)

    def reads(json: JsValue): JsResult[PaymentProcessor] = json match {
      case JsString(s) =>
        Try(PaymentProcessor.withName(s))
          .map(JsSuccess(_))
          .getOrElse(JsError("invalid.enum.format"))

      case _ => JsError(ValidationError("error.expected.string"))
    }
  }
}
