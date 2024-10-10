package services
import cats.effect._
import cats.implicits.toTraverseOps
import fs2._

import java.time.LocalDateTime
import javax.inject._
import models._
import services.{EventService, EventUserService, MessageService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class ReminderService @Inject() (
                                  eventService: EventService,
                                  eventUserService: EventUserService,
                                  messageService: MessageService
                                ) {

  def createReminderMessages: IO[Unit] = {
    val now = LocalDateTime.now()
    for {
      events <- IO.fromFuture(IO(eventService.getAll))
      _ <- events.filter(event => !event.startDateTime.isBefore(now) && !event.startDateTime.isAfter(now.plusMinutes(30)))
      .toList.traverse { event =>
        val userEventsIO: IO[Seq[EventUser]] = IO.fromFuture(IO(eventUserService.getAll))
        userEventsIO.flatMap { userEvents =>
          userEvents.filter(_.idEvent == event.id).toList.traverse { userEvent =>
            val message = Message(0, s"Reminder: Event ${event.name} is starts in less than 30 minutes!", userEvent.username, false)
            val createMessageIO: IO[Option[Message]] = IO.fromFuture(IO(messageService.create(message)))
            createMessageIO
          }.void
        }
      }.void
    } yield ()
  }

  def runStream: Stream[IO, Unit] =
    Stream.eval(createReminderMessages) ++
    Stream.awakeEvery[IO](30.minutes).evalMap(_ => createReminderMessages)
}
