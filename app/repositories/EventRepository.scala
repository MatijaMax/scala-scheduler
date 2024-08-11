package repositories

import models.Event
import org.postgresql.util.PSQLException
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import java.time.{Duration, LocalDateTime}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import zio.{Task, ZIO}


class EventRepository @Inject() (override protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val events = TableQuery[EventTable]

  def getAll: Future[Seq[Event]] = db.run(events.result)

  def getAllFiltered(lowerBound: LocalDateTime, upperBound: LocalDateTime): Future[Seq[Event]] = {
    val filteredQuery = events.filter { e =>
      e.startDateTime >= lowerBound && e.startDateTime <= upperBound
    }

    db.run(filteredQuery.result)
  }
  def getById(id: Long): Future[Option[Event]] = db.run(events.filter(_.id === id).result).map(_.headOption)

  def getByIdZIO(id: Long): ZIO[Any, Throwable, Option[Event]] = {
    ZIO.fromFuture { implicit ec =>
      db.run(events.filter(_.id === id).result).map(_.headOption)
    }
  }

  def insert(event: Event): Future[Option[Event]] = {
    val lowerBound = event.startDateTime
    val upperBound = event.startDateTime.plusMinutes(event.duration)

    getAll.flatMap { existingEvents =>
      val isOverlap = existingEvents.exists { e =>
        val existingLowerBound = e.startDateTime
        val existingUpperBound = e.startDateTime.plusMinutes(e.duration)
        lowerBound.isBefore(existingUpperBound) && upperBound.isAfter(existingLowerBound)
      }
      val result = isOverlap match {
        case true  => Future.successful(None)
        case false =>
          val insertAction = (events returning events.map(_.id) into ((event, id) => event.copy(id = id))) += event
          db.run(insertAction.transactionally.map(Some(_)))
      }
      result
    }
  }

  def getAllZIO: ZIO[Any, Throwable, Seq[Event]] = {
    ZIO.fromFuture { implicit ec =>
      db.run(events.result)
    }
  }

  def insertZIO(event: Event): ZIO[Any, Throwable, Option[Event]] = {
    val lowerBound = event.startDateTime
    val upperBound = event.startDateTime.plusMinutes(event.duration)

    for {
      existingEvents <- getAllZIO
      isOverlap = isEventOverlapping(existingEvents, lowerBound, upperBound)
      result <- if (isOverlap) {
        ZIO.succeed(None)
      } else {
        val insertAction = (events returning events.map(_.id) into ((event, id) => event.copy(id = id))) += event
        ZIO.fromFuture { implicit ec =>
          db.run(insertAction.transactionally).map(Some(_))
        }
      }
    } yield result
  }

  private def isEventOverlapping(existingEvents: Seq[Event], lowerBound: LocalDateTime, upperBound: LocalDateTime): Boolean = {
    existingEvents.exists { e =>
      val existingLowerBound = e.startDateTime
      val existingUpperBound = e.startDateTime.plusMinutes(e.duration)
      lowerBound.isBefore(existingUpperBound) && upperBound.isAfter(existingLowerBound)
    }
  }


  def delete(id: Long): Future[Option[Int]] = {
    db.run(events.filter(_.id === id).delete).map {
      case 0       => None
      case 1       => Some(1)
      case deleted => throw new RuntimeException(s"Deleted $deleted rows")
    }
  }

  //ZIO alternative
  def deleteZIO(id: Long): Task[Option[Int]] = {
    ZIO.fromFuture { implicit ec =>
      db.run(events.filter(_.id === id).delete).map {
        case 0 => None
        case 1 => Some(1)
        case deleted => throw new RuntimeException(s"Deleted $deleted rows")
      }
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
