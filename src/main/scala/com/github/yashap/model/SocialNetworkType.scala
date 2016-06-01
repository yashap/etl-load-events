package com.github.yashap.model

import play.api.data.validation.ValidationError
import play.api.libs.json._

import scala.util.Try

object SocialNetworkType extends Enumeration {
  type SocialNetworkType = Value

  val Twitter = Value("Twitter")
  val Facebook = Value("Facebook")
  val Instagram = Value("Instagram")
  val LinkedIn = Value("LinkedIn")

  implicit val socialNetworkTypeFormat = new Format[SocialNetworkType] {
    def writes(sn: SocialNetworkType): JsValue = JsString(sn.toString)

    def reads(json: JsValue): JsResult[SocialNetworkType] = json match {
      case JsString(s) =>
        Try(SocialNetworkType.withName(s))
          .map(JsSuccess(_))
          .getOrElse(JsError("invalid.enum.format"))

      case _ => JsError(ValidationError("error.expected.string"))
    }
  }
}