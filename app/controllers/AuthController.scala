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


  def create: Action[NewMessage] = basicAuthAction.async(parse.json[NewMessage]) { request =>
    val messageToAdd: Message = request.body

    messageService.create(messageToAdd).map {
      case Some(message) => Created(Json.toJson(message))
      case None          => Conflict
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

