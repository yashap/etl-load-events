package com.github.yashap.model

import com.github.yashap.spec.BaseSpec
import com.github.yashap.model.SocialNetworkType.LinkedIn
import com.github.yashap.model.PaymentProcessor.PayPal
import play.api.libs.json.Json

import java.time.LocalDateTime

class ExtractorSpec extends BaseSpec {

  val userEventWithoutExtraData = Json.obj(
    "event_id" -> "20bdfde2-ebd9-4aea-9351-7d2b84f436a3",
    "timestamp" -> "2016-02-23 08:11:20",
    "user_id" -> 863,
    "event" -> "web_logout"
  )
  val parsedUserEventWithoutExtraData = UserEvent(
    "20bdfde2-ebd9-4aea-9351-7d2b84f436a3",
    LocalDateTime.of(2016, 2, 23, 8, 11, 20),
    863,
    "web_logout",
    None
  )

  val userEventWithExtraData = Json.obj(
    "event_id" -> "fd84aa64-eb36-48fe-ba58-158682162944",
    "timestamp" -> "2016-02-23 08:14:47",
    "user_id" -> 4624,
    "event" -> "scroll_stream",
    "extra_data" -> Json.obj(
      "social_network_type" -> "LinkedIn"
    )
  )
  val parsedUserEventWithExtraData = UserEvent(
    "fd84aa64-eb36-48fe-ba58-158682162944",
    LocalDateTime.of(2016, 2, 23, 8, 14, 47),
    4624,
    "scroll_stream",
    Some(LinkedIn)
  )

  val organizationEvent = Json.obj(
    "event_id" -> "6f9a8ca3-4c20-421f-84eb-6fb7e7cf3ff9",
    "timestamp" -> "2016-02-23 10:21:39",
    "organization_id" -> 266,
    "event" -> "file_support_ticket"
  )
  val parsedOrganizationEvent = OrganizationEvent(
    "6f9a8ca3-4c20-421f-84eb-6fb7e7cf3ff9",
    LocalDateTime.of(2016, 2, 23, 10, 21, 39),
    266,
    "file_support_ticket"
  )

  val oranizationPayment = Json.obj(
    "event_id" -> "50037a4b-bbeb-45d6-8332-8572f37c0b4d",
    "timestamp" -> "2016-02-23 11:55:14",
    "organization_id" -> 175,
    "event" -> "paid_for_entperise_package",
    "extra_data" -> Json.obj(
      "payment_amount" -> 142120.09,
      "payment_processor" -> "PayPal"
    )
  )
  val parsedOrganizationPayment = OrganizationPayment(
    "50037a4b-bbeb-45d6-8332-8572f37c0b4d",
    LocalDateTime.of(2016, 2, 23, 11, 55, 14),
    175,
    "paid_for_entperise_package",
    142120.09,
    PayPal
  )

  val unknownEvent = Json.obj("some" -> "crap")

  "extractEvent" should "extract a user event that lacks extra data" in {
    Extractor.extractEvent(userEventWithoutExtraData) shouldBe parsedUserEventWithoutExtraData
  }

  it should "extract a user event that has extra data" in {
    Extractor.extractEvent(userEventWithExtraData) shouldBe parsedUserEventWithExtraData
  }

  it should "extract an organization event" in {
    Extractor.extractEvent(organizationEvent) shouldBe parsedOrganizationEvent
  }

  it should "extract an organization payment" in {
    Extractor.extractEvent(oranizationPayment) shouldBe parsedOrganizationPayment
  }

  it should "extract an unknown event" in {
    // readAt depends on the time the event was read, account for this
    val actualUnknownEvent: UnknownEvent = Extractor.extractEvent(unknownEvent).asInstanceOf[UnknownEvent]
    val expectedUnknownEvent = unknownEvent.validate[UnknownEvent].get.copy(readAt = actualUnknownEvent.readAt)

    actualUnknownEvent shouldBe expectedUnknownEvent
  }

  "extractEvents" should "extract all events in a sequence" in {
    val events = List(
      userEventWithoutExtraData,
      userEventWithExtraData,
      oranizationPayment,
      organizationEvent,
      organizationEvent,
      unknownEvent
    )

    val extractedEvents = Extractor.extractEvents(events)

    // readAt depends on the time the event was read, account for this
    val expectedEvents = ExtractedEvents(
      List(parsedUserEventWithExtraData, parsedUserEventWithoutExtraData),
      List(parsedOrganizationPayment),
      List(parsedOrganizationEvent, parsedOrganizationEvent),
      List(unknownEvent.validate[UnknownEvent].get)
    )
    val expectedEventsWithFix = expectedEvents.copy(
      unknownEvents = List(expectedEvents.unknownEvents.head.copy(readAt = extractedEvents.unknownEvents.head.readAt))
    )

    extractedEvents shouldBe expectedEventsWithFix
  }
}
