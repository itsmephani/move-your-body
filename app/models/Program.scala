package models

import play.api.libs.json.Json

case class Program(id: Long,
                   name: String,
                   description: String,
                   isPublic: Boolean,
                   userId: Long)

object Program {
   implicit val programFormat = Json.format[Program]
}
