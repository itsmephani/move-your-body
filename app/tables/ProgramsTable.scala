package tables

import models.Program
import Tables.users
import slick.jdbc.PostgresProfile.api._

class ProgramsTable(tag: Tag) extends Table[Program](tag, "programs") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def description = column[String]("description")

  def isPublic = column[Boolean]("isPublic")

  def userId = column[Long]("userId")

  def user = foreignKey("userId", userId, users)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  def * = (id, name, description, isPublic, userId) <> ((Program.apply _).tupled, Program.unapply)
}
