package dtos

import models.Event
import play.api.libs.json.{Json, Reads}
import scala.language.implicitConversions
import java.time.LocalDateTime

case class NewEvent(id: Long, name: String, description: String, creator: String, startDateTime: LocalDateTime, duration: Int)

  object NewEvent {
    implicit val jsonReader: Reads[NewEvent] = Json.reads[NewEvent]

    implicit def toModel(newEvent: NewEvent): Event = Event(0, newEvent.name, newEvent.description, newEvent.creator, newEvent.startDateTime, newEvent.duration)
}
