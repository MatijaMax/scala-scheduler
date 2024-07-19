package services
import models.Event
import repositories.EventRepository

import javax.inject.Inject
import scala.concurrent.Future
class EventService @Inject() (eventRepository: EventRepository){

  def getAll: Future[Seq[Event]] = eventRepository.getAll

  def getById(id: Long): Future[Option[Event]] = eventRepository.getById(id)

  def create(event: Event): Future[Option[Event]] = eventRepository.insert(event)

  def delete(id: Long): Future[Option[Int]] = eventRepository.delete(id)

  def update(id: Long, event: Event): Future[Option[Event]] = eventRepository.update(id, event)

}
