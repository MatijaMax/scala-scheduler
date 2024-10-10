package models
import play.api.libs.json.{Json, OFormat}

case class EventUser(id: Long, idEvent: Long, username: String)

object EventUser {
  implicit val jsonFormat: OFormat[EventUser] = Json.format[EventUser]
}



