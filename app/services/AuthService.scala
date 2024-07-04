package services

import com.github.t3hnar.bcrypt._
import helpers.BasicAuth
import models.User
import repositories.AuthRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class AuthService @Inject()(userRepository: AuthRepository)(implicit ec: ExecutionContext) {

  private val salt = "$2a$10$tdBXWNyNDnp0HbnjWtK3g."



  def basicAuth(auth: String): Future[Boolean] =
    BasicAuth.parse(auth) match {
      case Some((username, password)) => authenticate(username, password)
      case None => Future.successful(false)
    }

  private def authenticate(username: String, password: String): Future[Boolean] =
    userRepository
      .getByUsername(username)
      .map {
        case Some(user) => user.password == password.bcryptBounded(salt)
        case _ => false
      }

   def isUserUnique(username:String) : Future[Boolean] =
    userRepository
      .getByUsername(username)
      .map {
        case Some(user) => true
        case _ => false
      }

  def register(user: User): Future[Option[User]] = {
    val hashedPassword: Try[String] = user.password.bcryptSafe(salt)
    val userWithHashedPassword = user.copy(password = hashedPassword.getOrElse("defaultPassword"))
    userRepository.insert(userWithHashedPassword)
  }
}

