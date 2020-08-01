package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{BaseController, ControllerComponents}
import repositories.WeightRepository
import scala.concurrent.{ExecutionContext, Future}
import auth.AuthAction
import play.api.data.Forms._
import play.api.data.Form

@Singleton
class WeightController @Inject()(repo: WeightRepository,
                                 authAction: AuthAction,
                                 val controllerComponents: ControllerComponents)
                                 (implicit ec: ExecutionContext)
  extends BaseController {

  def list() = authAction.async { implicit userRequest =>
    repo.userWeights(userRequest.user.id)
      .map(weights => Ok(Json.toJson(weights)))
  }

  def create() = authAction.async { implicit userRequest =>
    try {
      val form = weightConstraints.bind(userRequest.body.asJson.get)

      if (form.hasErrors) {
        val messages = form.errors.map(error => error.key + " " + error.messages)
        Future(BadRequest(Json.toJson(messages)))
      } else {
        val programId = 1
        repo.create(userRequest.user.id, programId)
          .map { enrollment => Ok(Json.toJson(enrollment)) }
      }
    } catch {
      case _ : Throwable => Future(BadRequest("All fields are not passed"))
    }
  }

  private def weightConstraints = Form(
    tuple(
      "value" -> number.verifying(_ > 0),
      "dummy" -> default(text, "")
    )
  )
}

