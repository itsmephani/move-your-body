package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{BaseController, ControllerComponents, Result}
import repositories.WorkoutRepository

import scala.concurrent.{ExecutionContext, Future}
import auth.AuthAction

@Singleton
class WorkoutController @Inject()(repo: WorkoutRepository,
                                  authAction: AuthAction,
                                  val controllerComponents: ControllerComponents)(implicit  ec: ExecutionContext) extends BaseController {

  def get(programId: Long) = authAction.async { implicit userRequest =>
    repo.getProgramWorkouts(programId).map {
      case Some(workoutProgramAndUser) =>
        Ok(Json.toJson(workoutProgramAndUser._1)
          .as[JsObject]
          .++(Json.obj("program" -> workoutProgramAndUser._2))
          .++(Json.obj("user" -> workoutProgramAndUser._3)))
      case _ => NotFound
    }
  }

  def create(programId: Long) = authAction.async { implicit userRequest =>
    try {
      val name: String = userRequest.body.asJson.get("name").as[String]
      val description: String = userRequest.body.asJson.get("description").as[String]

      repo.create(name, description, programId).map { program =>
        Ok(Json.toJson(program))
      }
    } catch {
      case _ : Throwable => Future(BadRequest("All fields are not passed"))
    }
  }
}

