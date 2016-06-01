package com.github.yashap.model

import play.api.libs.json._

object Extractor {
  def extractEvents(jsons: Seq[JsValue]): ExtractedEvents = {
    val events: List[Product with Serializable] = jsons.map(extractEvent).toList

    ExtractedEvents(
      events.collect { case e: UserEvent => e },
      events.collect { case e: OrganizationPayment => e },
      events.collect { case e: OrganizationEvent => e },
      events.collect { case e: UnknownEvent => e }
    )
  }

  private def extractEvent(json: JsValue): Product with Serializable = {
    json.validate[UserEvent] match {
      case JsSuccess(userEvent, _) => userEvent
      case _ => json.validate[OrganizationPayment] match {
        case JsSuccess(orgPmt, _) => orgPmt
        case _ => json.validate[OrganizationEvent] match {
          case JsSuccess(orgEvent, _) => orgEvent
          case _ => json.validate[UnknownEvent].get
        }
      }
    }
  }
}

case class ExtractedEvents(
  userEvents: List[UserEvent],
  organizationPayments: List[OrganizationPayment],
  organizationEvents: List[OrganizationEvent],
  unknownEvents: List[UnknownEvent]
)