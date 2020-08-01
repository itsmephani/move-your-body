package tables

import models.Workout
import slick.jdbc.PostgresProfile.api._
import Tables.programs

class WorkoutsTable(tag: Tag) extends Table[Workout](tag, "workouts") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def description = column[String]("description")

  def programId = column[Long]("programId")

  def program =
    foreignKey("programId", programId, programs)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  def * = (id, name, description, programId) <> ((Workout.apply _).tupled, Workout.unapply)
}
