package com.github.yashap.dao

import slick.backend.DatabaseConfig
import slick.profile.BasicProfile

/**
 * Code taken from the Play framework's play.api.db.slick.HasDatabaseConfig, a trait to elegantly allow for multiple db
 * backends based on different configurations
 *
 * @tparam P a profile for accessing SQL databases, for example a slick.driver.JdbcProfile
 */
trait HasDatabaseConfig[P <: BasicProfile] {
  /** The Slick database configuration. */
  protected val dbConfig: DatabaseConfig[P] // field is declared as a val because we want a stable identifier.

  /** The Slick driver extracted from `dbConfig`. */
  protected final lazy val driver: P = dbConfig.driver // field is lazy to avoid early initializer problems.

  /** The Slick database extracted from `dbConfig`. */
  protected final def db: P#Backend#Database = dbConfig.db

  /** The original typesafe config that was used to generate the DatabaseConfig */
  protected final lazy val dbConf = dbConfig.config

}
