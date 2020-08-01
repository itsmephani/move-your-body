package repositories

import java.sql.Timestamp
import java.util.Date

import javax.inject.{Inject, Singleton}
import models.Weight
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.Tables.weights

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WeightRepository @Inject() (dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def create(userId: Long, value: Long): Future[Weight] = db.run {
    (weights.map(weight => (weight.userId, value, weight.createdAt))
      returning weights.map(_.id)
      into ((columns, id) => Weight(id, columns._1, columns._2, columns._3))
      ) += (userId, value, new Timestamp((new Date()).getTime()))
  }

  def insertOrUpdate(userId: Long, value: Long): Future[Unit] = db.run {
    val today = new Timestamp(new Date().getTime)
    weights.filter(weight => weight.userId === userId)
      .map { weight =>
        (weight.value, weight.createdAt)
      }
      .update(value, today)
      .map {
        case 0 => create(userId, value)
        case _ => Some(())
      }
  }

  def userWeights(userId: Long): Future[Seq[Weight]] = db.run {
    val query = weights.filter(weight => weight.userId === userId)

    query.result
  }
}
