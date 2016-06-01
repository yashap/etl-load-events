package com.github.yashap

import com.typesafe.config.Config

import scala.concurrent.duration._

package object config {

  implicit class ConfigMethods(val c: Config) {
    def getFiniteDuration(path: String, timeUnit: TimeUnit = NANOSECONDS): FiniteDuration = {
      FiniteDuration(c.getDuration(path, timeUnit), timeUnit)
    }
  }

}
