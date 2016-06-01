package com.github.yashap.app.lambda

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.beans.BeanProperty
import scala.util.Try

// vars required by jackson, for AWS Lambda
case class Request(@BeanProperty var runDate: String) {

  // Required by jackson, for AWS Lambda
  def this() = this("")
}

case class ImmutableRequest(runDate: LocalDate)

object ImmutableRequest {
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def from(r: Request): Try[ImmutableRequest] = Try {
    ImmutableRequest(LocalDate.parse(r.runDate, formatter))
  }
}
