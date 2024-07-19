package repositories

import models.Event
import org.postgresql.util.PSQLException
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import java.time.LocalDateTime

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class EventRepository @Inject() (override protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val events = TableQuery[EventTable]

  def getAll: Future[Seq[Event]] = db.run(events.result)

  def getById(id: Long): Future[Option[Event]] = db.run(events.filter(_.id === id).result).map(_.headOption)

  def insert(event: Event): Future[Option[Event]] =
    db.run((events returning events) += event)
      .map(Some.apply[Event])
      .recover { case e: PSQLException =>
        None
      }

  def delete(id: Long): Future[Option[Int]] = {
    db.run(events.filter(_.id === id).delete).map {
      case 0       => None
      case 1       => Some(1)
      case deleted => throw new RuntimeException(s"Deleted $deleted rows")
    }
  }

  def update(id: Long, event: Event): Future[Option[Event]] = {
    db.run(events.filter(_.id === id).update(event).map {
      case 0       => None
      case 1       => Some(event)
      case updated => throw new RuntimeException(s"Updated $updated rows")
    })
  }

  class EventTable(tag: Tag) extends Table[Event](tag, "events") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def description = column[String]("description")
    def creator = column[String]("creator")

    def startDateTime = column[LocalDateTime]("startdatetime")

    def duration = column[Int]("duration")
    override def * = (id, name, description, creator, startDateTime, duration) <> ((Event.apply _).tupled, Event.unapply)
  }

}
