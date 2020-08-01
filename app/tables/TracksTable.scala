package tables

import java.sql.Timestamp

import slick.jdbc.PostgresProfile.api._
import models.Tracker
import Tables.{users, workouts}

class TrackersTable(tag: Tag)  extends Table[Tracker](tag, "trackers") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def userId = column[Long]("userId")

  def workoutId = column[Long]("workoutId")

  def createdAt = column[Timestamp]("createdAt")

  def user = foreignKey("userId", userId, users)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  def workout = foreignKey("workoutId", workoutId, workouts)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  def * = (id, userId, workoutId, createdAt) <> ((Tracker.apply _).tupled, Tracker.unapply)
}
