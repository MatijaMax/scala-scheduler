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
    for {
      //GET EVENT INFO
      event <- eventService.getByIdZIO(eventId)
      event <- event match {
        case Some(e) => ZIO.succeed(e)
        case None => ZIO.fail(new RuntimeException("Not found"))
      }
      //DELETE EVENT
      deleteResult <- eventService.deleteZIO(eventId)
      _ <- deleteResult match {
        case Some(_) => ZIO.unit
        case None => ZIO.fail(new RuntimeException("Not found chained"))
      }
      //GET LINKER OBJECTS
      userEvents <- eventUserService.getByEventZIO(eventId)
      _ <- userEvents match {
        case seq if seq.nonEmpty => ZIO.unit
        case _ => ZIO.fail(new RuntimeException("Not found chained"))
      }
      //CREATE CANCELLATION MESSAGES
      messages <- messageService.createCancellationMessagesZIO(event.name, event.creator, userEvents.toList)
      _ <- messages match {
        case msg if msg.forall(_.isDefined) => ZIO.unit
        case _ => ZIO.fail(new RuntimeException("Not found chained"))
      }
      //DELETE LINKER OBJECTS
      deleteUserEventResult <- eventUserService.deleteByEventZIO(eventId)
      _ <- deleteUserEventResult match {
        case Some(_) => ZIO.unit
        case None => ZIO.fail(new RuntimeException("Not found"))
      }
    } yield ()

  }
}
