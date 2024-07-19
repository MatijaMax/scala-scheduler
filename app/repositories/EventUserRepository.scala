package repositories

import models.EventUser
import org.postgresql.util.PSQLException
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import java.time.LocalDateTime

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EventUserRepository @Inject() (override protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val eventUsers = TableQuery[EventUserTable]

  def getAll: Future[Seq[EventUser]] = db.run(eventUsers.result)

  def getById(id: Long): Future[Option[EventUser]] = db.run(eventUsers.filter(_.id === id).result).map(_.headOption)

  def insert(event: EventUser): Future[Option[EventUser]] =
    db.run((eventUsers returning eventUsers) += event)
      .map(Some.apply[EventUser])
      .recover { case e: PSQLException =>
        None
      }

  def delete(id: Long): Future[Option[Int]] = {
    db.run(eventUsers.filter(_.id === id).delete).map {
      case 0       => None
      case 1       => Some(1)
      case deleted => throw new RuntimeException(s"Deleted $deleted rows")
    }
  }

  def update(id: Long, event: EventUser): Future[Option[EventUser]] = {
    db.run(eventUsers.filter(_.id === id).update(event).map {
      case 0       => None
      case 1       => Some(event)
      case updated => throw new RuntimeException(s"Updated $updated rows")
    })
  }

  class EventUserTable(tag: Tag) extends Table[EventUser](tag, "eventUsers") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def idEvent = column[Long]("idEvent")
    def username = column[String]("username")

    override def * = (id, idEvent, username) <> ((EventUser.apply _).tupled, EventUser.unapply)
  }

}

