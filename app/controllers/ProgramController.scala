package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{BaseController, ControllerComponents}
import repositories.ProgramRepository

import scala.concurrent.ExecutionContext
import auth.AuthAction
import models.User
import play.api.libs.typedmap.TypedKey;

@Singleton
class ProgramController @Inject()(repo: ProgramRepository,
                                  authAction: AuthAction,
                                  val controllerComponents: ControllerComponents)(implicit  ec: ExecutionContext) extends BaseController {

  def get(id: Long) = authAction.async { implicit userRequest =>
    repo.get(id).map(programAndUser => Ok(Json.toJson(programAndUser._1)
      .as[JsObject]
      .++(Json.obj("user" -> programAndUser._2)))
    )
  }
}

