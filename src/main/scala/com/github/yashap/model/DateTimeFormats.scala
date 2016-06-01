package com.github.yashap.model

import play.api.data.validation.ValidationError
import play.api.libs.json._

import scala.util.Try
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

trait DateTimeFormats {
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  implicit val dateTimeFormat: Format[LocalDateTime] = new Format[LocalDateTime] {
    override def writes(dt: LocalDateTime): JsValue = JsString(dt.format(dateTimeFormatter))

    override def reads(json: JsValue): JsResult[LocalDateTime] = json match {
      case JsString(s) =>
        Try(LocalDateTime.parse(s, dateTimeFormatter))
          .map(JsSuccess(_))
          .getOrElse(JsError("invalid.date.format"))

      case _ => JsError(ValidationError("error.expected.date"))
    }
  }
}
