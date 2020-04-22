package repositories

import javax.inject.{Inject, Singleton}
import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def age = column[Int]("age")

    def * = (id, name, age) <> ((User.apply _).tupled, User.unapply)
  }

  val users = TableQuery[UsersTable]

  def create(name: String, age: Int): Future[User] = db.run {
    (users.map(user => (user.name, user.age))
      returning users.map(_.id)
      into ((nameAge, id) => User(id, nameAge._1, nameAge._2))
      ) += (name, age)
  }

  def list: Future[Seq[User]] = db.run {
    users.result
  }
}
