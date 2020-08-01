package repositories

import java.sql.Timestamp
import java.util.Date

import javax.inject.{Inject, Singleton}
import models.Tracker
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.Tables.trackers

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TrackerRepository @Inject() (dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def create(userId: Long, workoutId: Long): Future[Tracker] = db.run {
    (trackers.map(tracker => (tracker.userId, tracker.workoutId, tracker.createdAt))
      returning trackers.map(_.id)
      into ((columns, id) => Tracker(id, columns._1, columns._2, columns._3))
      ) += (userId, workoutId, new Timestamp((new Date()).getTime()))
  }

  def userTrackers(userId: Long): Future[Seq[Tracker]] = db.run {
    val query = trackers.filter(tracker => tracker.userId === userId)

    query.result
  }
}
