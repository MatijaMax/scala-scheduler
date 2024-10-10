package models

import play.api.libs.json.{Json, OFormat}

case class Message(id: Long, content: String, receiver: String, read: Boolean)

object Message {
  implicit val jsonFormat: OFormat[Message] = Json.format[Message]
}
