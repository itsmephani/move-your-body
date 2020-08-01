package tables

import java.sql.Timestamp

import slick.jdbc.PostgresProfile.api._
import models.User

class UsersTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def email = column[String]("email")

  def age = column[Int]("age")

  def weight = column[Int]("weight")

  def gender = column[String]("gender")

  def password = column[String]("password")

  def apiKey = column[String]("apiKey")

  def createdAt = column[Timestamp]("createdAt")

  def * =
    (id, name, email, age, weight, gender, password, apiKey, createdAt) <> ((User.apply _).tupled, User.unapply)
}
