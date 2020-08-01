package tables

import java.sql.Timestamp

import models.Weight
import slick.jdbc.PostgresProfile.api._
import Tables.users

class WeightsTable(tag: Tag)  extends Table[Weight](tag, "weights") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def userId = column[Long]("userId")

  def value = column[Float]("value")

  def createdAt = column[Timestamp]("createdAt")

  def user =
    foreignKey("userId", userId, users)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  def * = (id, userId, value, createdAt) <> ((Weight.apply _).tupled, Weight.unapply)
}
