package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{BaseController, ControllerComponents}
import repositories.{EnrollmentRepository, ProgramRepository}
import scala.concurrent.{ExecutionContext, Future}
import auth.AuthAction
import play.api.data.Forms._
import play.api.data.Form

@Singleton
class EnrollmentController @Inject()(repo: EnrollmentRepository,
                                     programRepo: ProgramRepository,
                                     authAction: AuthAction,
                                     val controllerComponents: ControllerComponents)
                                 (implicit ec: ExecutionContext)
  extends BaseController {

  def list() = authAction.async { implicit userRequest =>
    repo.userEnrollments(userRequest.user.id)
      .map(enrollments => Ok(Json.toJson(enrollments)))
  }

  def create() = authAction.async { implicit userRequest =>
    try {
      val form = enrollmentConstraints.bind(userRequest.body.asJson.get)

      if (form.hasErrors) {
        val messages = form.errors.map(error => error.key + " " + error.messages)
        Future(BadRequest(Json.toJson(messages)))
      } else {
        val programId: Int = form.data("programId").toInt
        programRepo.get(programId).flatMap {
          case (_, _) => repo.create(userRequest.user.id, programId)
              .map { enrollment => Ok(Json.toJson(enrollment)) }
          case _ => Future(BadRequest("Program not found"))
        }
      }
    } catch {
      case _ : Throwable => Future(BadRequest("All fields are not passed"))
    }
  }

  private def enrollmentConstraints = Form(
    tuple(
      "programId" -> number.verifying(_ > 0),
      "dummy" -> default(text, "")
    )
  )
}

