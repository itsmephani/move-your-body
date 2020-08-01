package models

import java.sql.Timestamp

import formatters.TimestampFormatter
import play.api.libs.json.Json

case class Tracker(id: Long,
                   userId: Long,
                   workoutId: Long,
                   createdAt: Timestamp)

object Tracker extends TimestampFormatter {
  implicit val trackerFormat = Json.format[Tracker]
}
