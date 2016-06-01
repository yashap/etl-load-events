package com.github.yashap.dao

import com.github.yashap.model.UnknownEvent
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{Future, ExecutionContext}
import scala.util.Success
import java.time.LocalDateTime

class UnknownEventDAO(val dbConfig: DatabaseConfig[JdbcProfile]) extends BaseDAO {
  import driver.api._

  val tableName = "unknown_event"

  private class UnknownEventTable(tag: Tag) extends Table[UnknownEvent](tag, tableName) {
    def id = column[String]("id", O.PrimaryKey)
    def readAt = column[LocalDateTime]("read_at")
    def eventBlob = column[String]("event_blob")

    def * = (id, readAt, eventBlob) <> (UnknownEvent.tupled, UnknownEvent.unapply)
  }

  private val unknownEventsTQ = TableQuery[UnknownEventTable]

  def upsert(unknownEvents: Seq[UnknownEvent])(implicit ec: ExecutionContext): Future[Int] = {
    val action = DBIO.seq(unknownEvents.map(unknownEventsTQ.insertOrUpdate): _*)
    db.run(action)
      .map(_ => unknownEvents.length)
      .andThen { case Success(n) =>
        logger.debug(s"Saved $n unknown events")
      }
  }
}
