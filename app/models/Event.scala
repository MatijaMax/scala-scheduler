package models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

case class Event(id: Long,
                 name: String,
                 description: String,
                 creator: String,
                 startDateTime: LocalDateTime,
                 duration: Int)
object Event {
  implicit val jsonFormat: OFormat[Event] = Json.format[Event]
}