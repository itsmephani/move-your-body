package models

import play.api.libs.json.Json

case class Workout(id: Long,
                   name: String,
                   description: String,
                   programId: Long)

object Workout {
  implicit val programFormat = Json.format[Workout]
}
