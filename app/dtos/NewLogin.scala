package dtos

import models.User
import play.api.libs.json.{Json, Reads}
import scala.language.implicitConversions

case class NewLogin(username: String, password: String)

object NewLogin {
  implicit val jsonReader: Reads[NewLogin] = Json.reads[NewLogin]

  implicit def toModel(newLogin: NewLogin): User = User(0, newLogin.username, newLogin.password)
}
