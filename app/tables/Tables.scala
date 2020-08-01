package tables

import slick.lifted.TableQuery

object Tables {
  lazy val enrollments = TableQuery[EnrollmentsTable]
  lazy val programs = TableQuery[ProgramsTable]
  lazy val trackers = TableQuery[TrackersTable]
  lazy val users = TableQuery[UsersTable]
  lazy val workouts = TableQuery[WorkoutsTable]
  lazy val weights = TableQuery[WeightsTable]
}
