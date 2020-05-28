package repositories

import java.sql.Timestamp
import java.util.Date

import javax.inject.{Inject, Singleton}
import models.{Enrollment, Program, User}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnrollmentRepository @Inject() (val programsRepo: ProgramRepository,
                                      val usersRepo: UserRepository,
                                      dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class EnrollmentsTable(tag: Tag) extends Table[Enrollment](tag, "enrollments") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("userId")

    def programId = column[Long]("programId")

    def startDate = column[Timestamp]("startDate")

    def user = foreignKey("userId", userId, usersRepo.users)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

    def program = foreignKey("programId", programId, programsRepo.programs)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

    def * = (id, userId, programId, startDate) <> ((Enrollment.apply _).tupled, Enrollment.unapply)
  }

  val enrollments = TableQuery[EnrollmentsTable]

  def create(userId: Long, programId: Long): Future[Enrollment] = db.run {
    (enrollments.map(enrollment => (enrollment.userId, enrollment.programId, enrollment.startDate))
      returning enrollments.map(_.id)
      into ((columns, id) => Enrollment(id, columns._1, columns._2, columns._3))
      ) += (userId, programId, new Timestamp((new Date()).getTime()))
  }

  def userEnrollments(userId: Long) = db.run {
    val query = for {
      enrollment <- enrollments if enrollment.userId === userId
    } yield enrollment

    query.result
  }
}
