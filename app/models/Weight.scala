package models

import java.sql.Timestamp
import formatters.TimestampFormatter
import play.api.libs.json.Json

case class Weight(id: Long,
                  userId: Long,
                  value: Float,
                  createdAt: Timestamp)

object Weight extends TimestampFormatter {
  implicit val weightFormat = Json.format[Weight]
}

