package repositories

import javax.inject.{Inject, Singleton}
import models.{Program, User, Workout}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WorkoutRepository @Inject() (programsRepo: ProgramRepository, dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class WorkoutsTable(tag: Tag) extends Table[Workout](tag, "workouts") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def description = column[String]("description")

    def programId = column[Long]("programId")

    def program = foreignKey("programId", programId, programsRepo.programs)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

    def * = (id, name, description, programId) <> ((Workout.apply _).tupled, Program.unapply)
  }

  val workouts = TableQuery[WorkoutsTable]

  def create(name: String, description: String, programId: Long): Future[Workout] = db.run {
    (workouts.map(workout => (workout.name, workout.description, workout.programId))
      returning workouts.map(_.id)
      into ((columns, id) => Workout(id, columns._1, columns._2, columns._3))
      ) += (name, description, programId)
  }

  def getProgramWorkouts(programId: Long): Future[(Workout, Program, User)] = db.run {
    val query = for {
      workout <- workouts if workout.programId === programId
      program <- workout.program
      user <- program.user
    } yield (workout, program, user)

    query.result.head
  }
}
