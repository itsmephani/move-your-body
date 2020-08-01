package repositories

import java.sql.Timestamp
import java.util.Date
import javax.inject.{Inject, Singleton}
import models.Enrollment
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.Tables.enrollments
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnrollmentRepository @Inject() (dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def create(userId: Long, programId: Long): Future[Enrollment] = db.run {
    (enrollments.map(enrollment => (enrollment.userId, enrollment.programId, enrollment.startDate))
      returning enrollments.map(_.id)
      into ((columns, id) => Enrollment(id, columns._1, columns._2, columns._3))
      ) += (userId, programId, new Timestamp((new Date()).getTime()))
  }

  def userEnrollments(userId: Long): Future[Seq[Enrollment]] = db.run {
    val query = enrollments.filter(enrollment => enrollment.userId === userId)

    query.result
  }
}