package repositories

import javax.inject.{Inject, Singleton}
import models.{Program, User}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.Tables.programs
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProgramRepository @Inject() (dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

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

