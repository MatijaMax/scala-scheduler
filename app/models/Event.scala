package models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

case class Event(id: Long,
                 name: String,
                 service: String,
                 creator: User,
                 listOfParticipants: List[User],
                 startDateTime: LocalDateTime,
                 endDateTime: LocalDateTime)
object Event {
  implicit val jsonFormat: OFormat[User] = Json.format[User]
}