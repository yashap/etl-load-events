package com.github.yashap.model

import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDateTime

case class OrganizationEvent(
  id: String,
  timestamp: LocalDateTime,
  organizationId: Long,
  event: String
)

object OrganizationEvent extends DateTimeFormats {
  def tupled = (OrganizationEvent.apply _).tupled

  implicit val organizationEventFormat: Format[OrganizationEvent] = (
    (JsPath \ "event_id").format[String] and
    (JsPath \ "timestamp").format[LocalDateTime] and
    (JsPath \ "organization_id").format[Long] and
    (JsPath \ "event").format[String]
  )(OrganizationEvent.apply, unlift(OrganizationEvent.unapply))
}
