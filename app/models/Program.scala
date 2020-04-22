package models

import play.api.libs.json.Json

case class Program(val id: Long,
                   val name: String,
                   val description: String,
                   val isPublic: Boolean,
                   val userId: Long)

object Program {
  implicit val programFormat = Json.format[Program]
}
