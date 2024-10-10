package controllers

import actions.BasicAuthAction
import dtos.{NewEvent, NewEventUser}
import models.{Event, EventUser}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.{EventService, EventUserService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventUserController @Inject() (val controllerComponents: ControllerComponents, eventUserService: EventUserService, basicAuthAction: BasicAuthAction)(implicit ec: ExecutionContext)
  extends BaseController {

  def getAll: Action[AnyContent] = Action.async {
    eventUserService.getAll.map(eventUsers => Ok(Json.toJson(eventUsers)))
  }

  def getById(id: Long): Action[AnyContent] = Action.async {
    eventUserService.getById(id).map {
      case Some(eventUser) => Ok(Json.toJson(eventUser))
      case None          => NotFound(s"Event with id: $id doesn't exist")
    }
  }

  def create: Action[NewEventUser] = Action.async(parse.json[NewEventUser]) { request =>
    val eventUserToAdd: EventUser = request.body

    eventUserService.create(eventUserToAdd).map {
      case Some(eventToAdd) => Created(Json.toJson(eventToAdd))
      case None          => Conflict
    }
  }

  def delete(id: Long): Action[AnyContent] = basicAuthAction.async {
    eventUserService.delete(id).map {
      case Some(_) => NoContent
      case None    => NotFound
    }
  }

  def update(id: Long): Action[EventUser] = basicAuthAction.async(parse.json[EventUser]) { request =>
    if (id != request.body.id)
      Future.successful(BadRequest("Id in path must be equal to id in body"))
    else
      eventUserService.update(id, request.body).map {
        case Some(event) => Ok(Json.toJson(event))
        case None          => NotFound
      }
  }

}
