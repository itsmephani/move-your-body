package models

import java.sql.Timestamp

import formatters.TimestampFormatter
import play.api.libs.json.Json

case class Enrollment(id: Long,
                      userId: Long,
                      programId: Long,
                      startDate: Timestamp)

object Enrollment extends TimestampFormatter {
  implicit val enrollmentFormat = Json.format[Enrollment]
}
