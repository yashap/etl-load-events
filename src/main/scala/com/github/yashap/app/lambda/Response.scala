package com.github.yashap.app.lambda

import scala.beans.BeanProperty

// vars required by jackson, for AWS Lambda
case class Response(@BeanProperty var code: Int, @BeanProperty var message: String) {

  // No param constructor required by jackson, for AWS Lambda
  def this() = this(200, "")
}
