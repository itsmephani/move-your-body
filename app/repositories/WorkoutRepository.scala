package repositories

import javax.inject.{Inject, Singleton}
import models.{Program, User, Workout}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.Tables.workouts
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WorkoutRepository @Inject() (val programsRepo: ProgramRepository, dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def create(name: String, description: String, programId: Long): Future[Workout] = db.run {
    (workouts.map(workout => (workout.name, workout.description, workout.programId))
      returning workouts.map(_.id)
      into ((columns, id) => Workout(id, columns._1, columns._2, columns._3))
      ) += (name, description, programId)
  }

  def getProgramWorkouts(programId: Long): Future[Option[(Workout, Program, User)]] = db.run {
    val query = for {
      workout <- workouts if workout.programId === programId
      program <- workout.program
      user <- program.user
    } yield (workout, program, user)

    query.result.headOption
  }
}
