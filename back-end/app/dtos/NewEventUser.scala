package dtos

import models.EventUser
import play.api.libs.json.{Json, Reads}

import scala.language.implicitConversions

case class NewEventUser(id: Long, idEvent: Long, username: String)

object NewEventUser {
  implicit val jsonReader: Reads[NewEventUser] = Json.reads[NewEventUser]

  implicit def toModel(newEventUser: NewEventUser): EventUser = EventUser(0, newEventUser.idEvent, newEventUser.username)
}
