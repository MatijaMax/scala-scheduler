package actions

import play.api.mvc.Results.Unauthorized
import play.api.mvc._
import services.AuthService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

class BasicAuthAction @Inject()(
  parser: BodyParsers.Default,
  authService: AuthService
)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    //Auth header
    request.headers.get("Authorization") match {
      case Some(auth) =>
        //"Basic" check
        if (auth.startsWith("Basic ")) {
          //base64
          val base64Credentials = auth.stripPrefix("Basic ")
          authService.basicAuth(base64Credentials).flatMap {
            case true => block(request)
            case false => Future.successful(Results.Unauthorized("Invalid credentials"))
          }
        } else {
          Future.successful(Results.Unauthorized("Invalid authorization header format"))
        }
      case None =>
        Future.successful(Results.Unauthorized("No authorization header found"))
    }
  }
}
