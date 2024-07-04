package repositories

import models.User
import org.postgresql.util.PSQLException
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val userTable = TableQuery[UserTable]

  def getByUsername(username: String): Future[Option[User]] =
    db.run {
      userTable
        .filter(_.username === username)
        .result
        .headOption
    }
  def insert(user: User): Future[Option[User]] =
    db.run((userTable returning userTable) += user)
      .map(Some.apply[User])
      .recover { case e: PSQLException =>
        None
      }

  def getAll: Future[Seq[User]] = db.run(userTable.result)



  class UserTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def username = column[String]("username")
    def password = column[String]("password")
    override def * : ProvenShape[User] = (id, username, password) <> ((User.apply _).tupled, User.unapply)
  }



}
