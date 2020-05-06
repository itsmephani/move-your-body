package repositories

import javax.inject.{Inject, Singleton}
import models.{Program, User}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProgramRepository @Inject() (usersRepo: UserRepository, dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class ProgramsTable(tag: Tag) extends Table[Program](tag, "programs") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def description = column[String]("description")

    def isPublic = column[Boolean]("isPublic")

    def userId = column[Long]("userId")

    def user = foreignKey("userId", userId, usersRepo.users)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

    def * = (id, name, description, isPublic, userId) <> ((Program.apply _).tupled, Program.unapply)
  }

  private val programs = TableQuery[ProgramsTable]

  def create(name: String, description: String, userId: Long, isPublic: Boolean = false): Future[Program] = db.run {
    (programs.map(program => (program.name, program.description, program.isPublic, program.userId))
      returning programs.map(_.id)
      into ((columns, id) => Program(id, columns._1, columns._2, columns._3, columns._4))
      ) += (name, description, isPublic, userId)
  }

  def list: Future[Seq[Program]] = db.run {
    programs.result
  }

  def get(id: Long): Future[(Program, User)] = db.run {
    val query = for {
      program <- programs if program.id === id
      user <- program.user
    } yield (program, user)

    query.result.statements.foreach(println)

    query.result.head
  }

  def getUserPrograms(userId: Long) = db.run {
    val query = for {
      program <- programs if program.userId === userId
    } yield program

    query.result
  }
}
