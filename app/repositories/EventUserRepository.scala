package repositories

import models.EventUser
import org.postgresql.util.PSQLException
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import zio.{Task, ZIO}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EventUserRepository @Inject() (override protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val eventUsers = TableQuery[EventUserTable]

  def getAll: Future[Seq[EventUser]] = db.run(eventUsers.result)


  def getByEventZIO(idEvent: Long): Task[Seq[EventUser]] = {
    ZIO.fromFuture { implicit ec =>
      db.run(eventUsers.filter(_.idEvent === idEvent).result)
    }
  }

  def getById(id: Long): Future[Option[EventUser]] = db.run(eventUsers.filter(_.id === id).result).map(_.headOption)



  def insert(eventUser: EventUser): Future[Option[EventUser]] = {

    val checkExists = eventUsers.filter(eu => eu.idEvent === eventUser.idEvent && eu.username === eventUser.username).exists.result

    val insertAction = (eventUsers returning eventUsers.map(_.id) into ((userEvent, id) => userEvent.copy(id = id))) += eventUser

    db.run(checkExists.flatMap {
      case true => DBIO.successful(None)
      case false => insertAction.map(Some(_))
    }.transactionally)
  }

  def delete(id: Long): Future[Option[Int]] = {
    db.run(eventUsers.filter(_.id === id).delete).map {
      case 0       => None
      case 1       => Some(1)
      case deleted => throw new RuntimeException(s"Deleted $deleted rows")
    }
  }


  //ZIO
  def deleteByEventZIO(idEvent: Long): Task[Option[Int]] = {
    ZIO.fromFuture { implicit ec =>
      db.run(eventUsers.filter(_.idEvent === idEvent).delete).map {
        case 0 => None
        case deleted => Some(deleted)
      }
    }
  }

  def update(id: Long, event: EventUser): Future[Option[EventUser]] = {
    db.run(eventUsers.filter(_.id === id).update(event).map {
      case 0       => None
      case 1       => Some(event)
      case updated => throw new RuntimeException(s"Updated $updated rows")
    })
  }

  class EventUserTable(tag: Tag) extends Table[EventUser](tag, "eventusers") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def idEvent = column[Long]("idevent")
    def username = column[String]("username")

    override def * = (id, idEvent, username) <> ((EventUser.apply _).tupled, EventUser.unapply)
  }

}

