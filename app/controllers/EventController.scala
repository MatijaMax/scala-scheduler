package controllers

import actions.BasicAuthAction
import dtos.NewEvent
import libs.http.ActionBuilderOps
import models.Event
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import play.twirl.api.TemplateMagic.anyToDefault
import services.{EventService, ZIOService}
import zio.{URIO, ZIO}

import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventController @Inject() (val controllerComponents: ControllerComponents,zioService: ZIOService, eventService: EventService, basicAuthAction: BasicAuthAction)(implicit ec: ExecutionContext)
  extends BaseController {

  def getAll: Action[AnyContent] = Action.async {
    eventService.getAll.map(events => Ok(Json.toJson(events)))
  }

  def getAllFiltered(lb: LocalDateTime, ub: LocalDateTime): Action[AnyContent] = Action.async {
    eventService.getAllFiltered(lb, ub).map(events => Ok(Json.toJson(events)))
  }

  def getById(id: Long): Action[AnyContent] = Action.async {
    eventService.getById(id).map {
      case Some(event) => Ok(Json.toJson(event))
      case None => NotFound(s"Event with id: $id doesn't exist")
    }
  }

  def create: Action[NewEvent] = Action.async(parse.json[NewEvent]) { request =>
    val eventToAdd: Event = request.body
    eventService.create(eventToAdd).map {
      case Some(eventToAdd) => Created(Json.toJson(eventToAdd))
      case None => Conflict
    }
  }

  def delete(id: Long): Action[AnyContent] = basicAuthAction.async {
    eventService.delete(id).map {
      case Some(_) => NoContent
      case None => NotFound
    }
  }

  def update(id: Long): Action[Event] = basicAuthAction.async(parse.json[Event]) { request =>
    if (id != request.body.id)
      Future.successful(BadRequest("Id in path must be equal to id in body"))
    else
      eventService.update(id, request.body).map {
        case Some(event) => Ok(Json.toJson(event))
        case None => NotFound
      }
  }


  def deleteZIO(id: Long): Action[AnyContent] = Action.zio { _ =>
    val result: ZIO[Any, Throwable, Unit] = zioService.deleteEventChained(id)

    result.fold(
      ex => InternalServerError(ex.getMessage),
      _ => NoContent
    )
  }
}
