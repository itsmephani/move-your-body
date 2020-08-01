package repositories

import java.sql.Timestamp
import java.util.{Date, UUID}

import javax.inject.{Inject, Singleton}
import models.User
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import tables.Tables.users

@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  def list: Future[Seq[User]] = db.run {
    users.result
  }

  def create(name: String, email: String, age: Int, weight: Int, gender: String, password: String): Future[User] = db.run {
    val encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
    val apiKey = UUID.randomUUID.toString

    (users.map(u => (u.name, u.email, u.age, u.weight, u.gender, u.password, u.apiKey, u.createdAt))
      returning users.map(_.id)
      into ((cols, id) => User(id, cols._1, cols._2, cols._3, cols._4, cols._5, cols._6, cols._7, cols._8))
      ) += (name, email, age, weight, gender, encryptedPassword, apiKey, new Timestamp((new Date()).getTime()))
  }

  def findByEmail(email: String): Future[Option[User]] = db.run {
    users.filter(user => user.email === email).result.headOption
  }

  def findByApiKey(apiKey: String): Future[Option[User]] = db.run {
    users.filter(user => user.apiKey === apiKey).result.headOption
  }
}
