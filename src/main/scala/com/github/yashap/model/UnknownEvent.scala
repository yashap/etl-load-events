package com.github.yashap.model

import org.apache.commons.codec.digest.DigestUtils
import play.api.libs.json._

import java.time.{LocalDateTime, ZoneOffset}

case class UnknownEvent(
  id: String,
  readAt: LocalDateTime,
  eventBlob: String
)

object UnknownEvent extends DateTimeFormats {
  def tupled = (UnknownEvent.apply _).tupled

  implicit val unknownEventFormat: Format[UnknownEvent] = new Format[UnknownEvent] {
    override def writes(unknownEvent: UnknownEvent): JsValue = Json.obj(
      "id" -> Json.toJson(unknownEvent.id),
      "read_at" -> Json.toJson(unknownEvent.readAt),
      "event_blob" -> Json.toJson(unknownEvent.eventBlob)
    )

    /**
     * Read an UnknownEvent from a JsValue. Note that this never fails, any JsValue can be read as an UnknownEvent. It
     * is intended to be use as a catch all class for any json that cannot otherwise be read.
     *
     * @param json json to read
     * @return a successfully read UnknownEvent
     */
    override def reads(json: JsValue): JsResult[UnknownEvent] = JsSuccess(UnknownEvent(
      DigestUtils.md5Hex(json.toString()),
      LocalDateTime.now(ZoneOffset.UTC),
      json.toString()
    ))
  }
}
