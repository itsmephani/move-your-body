package models

import java.sql.Timestamp

import formatters.TimestampFormatter
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json._

case class User(id: Long,
                name: String,
                email: String,
                age: Int,
                weight: Int,
                gender: String,
                password: String,
                apiKey: String,
                createdAt: Timestamp) {
  def isValidPassword(userPassword: String) = BCrypt.checkpw(userPassword, password)
}

object User extends TimestampFormatter {
  implicit val userFormat = Json.format[User]
}
