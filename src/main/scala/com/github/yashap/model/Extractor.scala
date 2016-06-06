package com.github.yashap.model

import play.api.libs.json._

object Extractor {
  def extractEvents(jsons: Seq[JsValue]): ExtractedEvents = {
    jsons.foldLeft(ExtractedEvents()){ (accum, json) =>
      extractEvent(json) match {
        case e: UserEvent => accum.copy(userEvents = e :: accum.userEvents)
        case e: OrganizationPayment => accum.copy(organizationPayments = e :: accum.organizationPayments)
        case e: OrganizationEvent => accum.copy(organizationEvents = e :: accum.organizationEvents)
        case e: UnknownEvent => accum.copy(unknownEvents = e :: accum.unknownEvents)
      }
    }
  }

  private[model] def extractEvent(json: JsValue): Product with Serializable = {
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
  userEvents: List[UserEvent] = Nil,
  organizationPayments: List[OrganizationPayment] = Nil,
  organizationEvents: List[OrganizationEvent] = Nil,
  unknownEvents: List[UnknownEvent] = Nil
)
