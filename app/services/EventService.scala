package services
import models.Event
import repositories.EventRepository
import zio.Task

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.Future
class EventService @Inject() (eventRepository: EventRepository){

  def getAll: Future[Seq[Event]] = eventRepository.getAll

  def getAllFiltered(lowerBound: LocalDateTime, upperBound:LocalDateTime): Future[Seq[Event]] = eventRepository.getAllFiltered(lowerBound, upperBound)

  def getById(id: Long): Future[Option[Event]] = eventRepository.getById(id)

  def create(event: Event): Future[Option[Event]] = eventRepository.insert(event)

  def delete(id: Long): Future[Option[Int]] = eventRepository.delete(id)

  def update(id: Long, event: Event): Future[Option[Event]] = eventRepository.update(id, event)

  def deleteZIO(id: Long): Task[Option[Int]] = eventRepository.deleteZIO(id)

  def getByIdZIO(id: Long): Task[Option[Event]] = eventRepository.getByIdZIO(id);

}
