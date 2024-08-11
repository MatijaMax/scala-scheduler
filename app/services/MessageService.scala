package services

import models.{EventUser, Message}
import repositories.MessageRepository
import zio.{Task, ZIO}

import javax.inject.Inject
import scala.concurrent.Future

class MessageService @Inject() (messageRepository: MessageRepository) {

  def getAll: Future[Seq[Message]] = messageRepository.getAll

  def getById(id: Long): Future[Option[Message]] = messageRepository.getById(id)

  def create(message: Message): Future[Option[Message]] = messageRepository.insert(message)

  def delete(id: Long): Future[Option[Int]] = messageRepository.delete(id)

  def update(id: Long, message: Message): Future[Option[Message]] = messageRepository.update(id, message)

  def createZIO(message: Message): Task[Option[Message]] = messageRepository.insertZIO(message)

  def createCancellationMessagesZIO(eventName: String, creatorName: String, userEvents: List[EventUser]): ZIO[Any, Throwable, List[Option[Message]]] = {
    for {
      messages <- ZIO.foreach(userEvents) { userEvent =>
        val message = Message(0, creatorName+": Event " + eventName + " was cancelled", userEvent.username, false)
        createZIO(message)
      }
    } yield messages
  }

}
