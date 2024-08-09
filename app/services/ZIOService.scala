package services

import models.Message
import zio._
import zio.ZIO

import javax.inject.Inject
import scala.concurrent.Future


class ZIOService @Inject() (
                             eventService: EventService,
                             messageService: MessageService,
                             eventUserService: EventUserService
                           ) {
  def deleteEventChained(eventId: Long): ZIO[Any, Throwable, Unit] = {
    val zioEffect: ZIO[Any, Throwable, Unit] = for {
      //GET EVENT INFO
      event <- eventService.getByIdZIO(eventId)
      event <- event match {
        case Some(e) => ZIO.succeed(e)
        case None => ZIO.fail(new RuntimeException("Event not found"))
      }
      //DELETE EVENT
      deleteResult <- eventService.deleteZIO(eventId)
      _ <- deleteResult match {
        case Some(_) => ZIO.unit
        case None => ZIO.fail(new RuntimeException("Event not found"))
      }
      //GET HELPERS
      userEvents <- eventUserService.getByEventZIO(eventId)
      _ <- userEvents match {
        case seq if seq.nonEmpty => ZIO.unit
        case _ => ZIO.fail(new RuntimeException("Helper not found"))
      }

      // CANCELLATION MESSAGES
      messages <- messageService.createCancellationMessagesZIO(event.name, userEvents.toList)

      // MESSAGE CREATION SUCCESS
      _ <- messages match {
        case msg if msg.forall(_.isDefined) => ZIO.unit
        case _ => ZIO.fail(new RuntimeException("Failed to create some cancellation messages"))
      }

      //DELETE HELPER OBJECTS
      deleteUserEventResult <- eventUserService.deleteByEventZIO(eventId)
      _ <- deleteUserEventResult match {
        case Some(_) => ZIO.unit
        case None => ZIO.fail(new RuntimeException("Failed to delete user events"))
      }
    } yield ()
    zioEffect.fold(
      ex => Future.failed(ex), // Convert failure to Future.failed
      _ => Future.successful(()) // Convert success to Future.successful
    )

  }


}
