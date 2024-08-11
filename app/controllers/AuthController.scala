package controllers

import actions.BasicAuthAction
import dtos.{NewLogin, NewMessage}
import models.Message
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.MessageService
import services.AuthService
import helpers.BasicAuth

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthController @Inject()(val controllerComponents: ControllerComponents, messageService: MessageService, authService: AuthService, basicAuthAction: BasicAuthAction)(implicit ec: ExecutionContext)
  extends BaseController {

  def register: Action[NewLogin] = Action.async(parse.json[NewLogin]) { request =>
    val userToAdd: NewLogin = request.body
    authService.isUserUnique(userToAdd.username).flatMap {
      case false =>
        authService.register(userToAdd).map {
          case Some(message) => Created(Json.toJson(message))
          case None => InternalServerError
        }
      case true =>
        Future.successful(Conflict("Username already exists"))
    }
  }

  def login(): Action[NewLogin] = Action.async(parse.json[NewLogin]) { implicit request =>
    val loginRequest = request.body

    val authString = s"Basic ${BasicAuth.createBasicAuthString(loginRequest.username, loginRequest.password)}"
    authService.basicAuth(authString).map {
      case true => Ok(Json.obj(
        "message" -> "Login successful",
        "username" -> loginRequest.username,
        "authString" -> authString
      ))
      case false => Unauthorized("Invalid username or password")
    }
  }
}

