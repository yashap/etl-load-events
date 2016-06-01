package com.github.yashap.dao

import com.github.yashap.model._
import com.github.yashap.model.SocialNetworkType.SocialNetworkType
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{Future, ExecutionContext}
import scala.util.Success
import java.time.LocalDateTime

class UserEventDAO(val dbConfig: DatabaseConfig[JdbcProfile]) extends BaseDAO {
  import driver.api._

  val tableName = "user_event"

  private class UserEventTable(tag: Tag) extends Table[UserEvent](tag, tableName) {
    def id = column[String]("id", O.PrimaryKey)
    def timestamp = column[LocalDateTime]("timestamp")
    def userId = column[Long]("user_id")
    def event = column[String]("event")
    def socialNetworkType = column[Option[SocialNetworkType]]("social_network_type")

    def * = (id, timestamp, userId, event, socialNetworkType) <> (UserEvent.tupled, UserEvent.unapply)
  }

  private val userEventsTQ = TableQuery[UserEventTable]

  def upsert(userEvents: Seq[UserEvent])(implicit ec: ExecutionContext): Future[Int] = {
    val action = DBIO.seq(userEvents.map(userEventsTQ.insertOrUpdate): _*)
    db.run(action)
      .map(_ => userEvents.length)
      .andThen { case Success(n) =>
        logger.debug(s"Saved $n user events")
      }
  }
}
