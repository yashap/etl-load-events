package com.github.yashap.model

import com.github.yashap.model.SocialNetworkType.SocialNetworkType
import play.api.libs.json._
import play.api.libs.functional.syntax._

import java.time.LocalDateTime

case class UserEvent(
  id: String,
  timestamp: LocalDateTime,
  userId: Long,
  event: String,
  socialNetworkType: Option[SocialNetworkType]
)

object UserEvent extends DateTimeFormats {
  def tupled = (UserEvent.apply _).tupled

  val userEventReads: Reads[UserEvent] = (
    (JsPath \ "event_id").read[String] and
    (JsPath \ "timestamp").read[LocalDateTime] and
    (JsPath \ "user_id").read[Long] and
    (JsPath \ "event").read[String] and
    (JsPath \ "extra_data" \ "social_network_type").readNullable[SocialNetworkType].orElse(Reads.pure(None))
  )(UserEvent.apply _)

  val userEventWrites: Writes[UserEvent] = (
    (JsPath \ "event_id").write[String] and
    (JsPath \ "timestamp").write[LocalDateTime] and
    (JsPath \ "user_id").write[Long] and
    (JsPath \ "event").write[String] and
    (JsPath \ "extra_data" \ "social_network_type").writeNullable[SocialNetworkType]
  )(unlift(UserEvent.unapply))

  implicit val userEventFormat: Format[UserEvent] = Format(userEventReads, userEventWrites)
}
