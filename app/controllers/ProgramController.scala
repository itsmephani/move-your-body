package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{BaseController, ControllerComponents, Result}
import repositories.ProgramRepository
import scala.concurrent.{ExecutionContext, Future}
import auth.AuthAction
import models.Program

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

  def create() = authAction.async { implicit userRequest =>
    try {
      val name: String = userRequest.body.asJson.get("name").as[String]
      val description: String = userRequest.body.asJson.get("description").as[String]
      val isPublic: Boolean = userRequest.body.asJson.map { json =>
            (json \ "isPublic").as[Boolean]
          }.getOrElse {
              false
          }

      repo.create(name, description, userRequest.user.id, isPublic).map { program =>
        Ok(Json.toJson(program))
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

