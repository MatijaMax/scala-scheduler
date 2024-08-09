package services
import models.EventUser
import repositories.EventUserRepository
import zio.Task

import javax.inject.Inject
import scala.concurrent.Future
class EventUserService @Inject() (eventUserRepository: EventUserRepository){

  def getAll: Future[Seq[EventUser]] = eventUserRepository.getAll

  def getById(id: Long): Future[Option[EventUser]] = eventUserRepository.getById(id)

  def create(message: EventUser): Future[Option[EventUser]] = eventUserRepository.insert(message)

  def delete(id: Long): Future[Option[Int]] = eventUserRepository.delete(id)

  def update(id: Long, eventUser: EventUser): Future[Option[EventUser]] = eventUserRepository.update(id, eventUser)

  def deleteByEventZIO(id: Long): Task[Option[Int]] = eventUserRepository.deleteByEventZIO(id)

  def getByEventZIO(idEvent: Long): Task[Seq[EventUser]] = eventUserRepository.getByEventZIO(idEvent)

}
