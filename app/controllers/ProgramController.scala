package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{BaseController, ControllerComponents}
import play.mvc.Security
import repositories.ProgramRepository

import scala.concurrent.ExecutionContext

@Singleton
class ProgramController @Inject()(repo: ProgramRepository, val controllerComponents: ControllerComponents)(implicit  ec: ExecutionContext) extends  BaseController {

  def get(id: Long) = Action.async { implicit request =>
    repo.get(id).map(programAndUser => Ok(Json.toJson(programAndUser._1)
      .as[JsObject]
      .++(Json.obj("user" -> programAndUser._2)))
    )
  }
}
