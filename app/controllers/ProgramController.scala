package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, JsObject, Json}
import play.api.mvc.{BaseController, ControllerComponents, Result}
import repositories.ProgramRepository
import scala.concurrent.{ExecutionContext, Future}
import auth.AuthAction
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.data.Form

@Singleton
class ProgramController @Inject()(repo: ProgramRepository,
                                  authAction: AuthAction,
                                  val controllerComponents: ControllerComponents)
                                 (implicit ec: ExecutionContext)
  extends BaseController {

  val createProgramConstraints = Form(
    tuple(
      "name" -> text.verifying(nonEmpty),
      "description"  -> text.verifying(nonEmpty),
      "isPublic" -> default(boolean, false)
    )
  )

  def get(id: Long) = authAction.async { implicit userRequest =>
    repo.get(id).map(programAndUser => Ok(Json.toJson(programAndUser._1)
      .as[JsObject]
      .++(Json.obj("user" -> programAndUser._2)))
    )
  }

  def create() = authAction.async { implicit userRequest =>
    try {
      val form = createProgramConstraints.bind(userRequest.body.asJson.get)

      if (form.hasErrors) {
        val messages = form.errors.map(error => error.key + " " + error.messages)
        Future(BadRequest(Json.toJson(messages)))
      } else {
        val name: String = form.data("name")
        val description: String = form.data("description")
        val isPublic: Boolean = form.value.get._3

        repo.create(name, description, userRequest.user.id, isPublic).map { program =>
          Ok(Json.toJson(program))
        }
      }
    } catch {
      case _ : Throwable => Future(BadRequest("All fields are not passed"))
    }
  }

  def getMyPrograms() = authAction.async { implicit userRequest =>
    repo.getUserPrograms(userRequest.id).map(program => Ok(Json.toJson(program)))
  }

  def getUserPrograms(id: Long) = authAction.async { implicit userRequest =>
    repo.getUserPrograms(id).map(program => Ok(Json.toJson(program)))
  }
}

