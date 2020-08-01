package tables

import java.sql.Timestamp

import models.Enrollment
import Tables.{users, programs}
import slick.jdbc.PostgresProfile.api._

class EnrollmentsTable(tag: Tag)  extends Table[Enrollment](tag, "enrollments") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def userId = column[Long]("userId")

  def programId = column[Long]("programId")

  def startDate = column[Timestamp]("startDate")

  def user =
    foreignKey("userId", userId, users)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  def program =
    foreignKey("programId", programId, programs)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  def * =
    (id, userId, programId, startDate) <> ((Enrollment.apply _).tupled, Enrollment.unapply)
}

