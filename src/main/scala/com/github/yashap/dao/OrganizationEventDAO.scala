package com.github.yashap.dao

import com.github.yashap.model.OrganizationEvent
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{Future, ExecutionContext}
import scala.util.Success
import java.time.LocalDateTime

class OrganizationEventDAO(val dbConfig: DatabaseConfig[JdbcProfile]) extends BaseDAO {
  import driver.api._

  val tableName = "organization_event"

  private class OrganizationEventTable(tag: Tag) extends Table[OrganizationEvent](tag, tableName) {
    def id = column[String]("id", O.PrimaryKey)
    def timestamp = column[LocalDateTime]("timestamp")
    def organizationId = column[Long]("organization_id")
    def event = column[String]("event")

    def * = (
      id, timestamp, organizationId, event) <> (OrganizationEvent.tupled, OrganizationEvent.unapply)
  }

  private val organizationEventsTQ = TableQuery[OrganizationEventTable]

  def upsert(organizationEvents: Seq[OrganizationEvent])(implicit ec: ExecutionContext): Future[Int] = {
    val action = DBIO.seq(organizationEvents.map(organizationEventsTQ.insertOrUpdate): _*)
    db.run(action)
      .map(_ => organizationEvents.length)
      .andThen { case Success(n) =>
        logger.debug(s"Saved $n organization events")
      }
  }
}
