package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{BaseController, ControllerComponents}
import play.api.libs.json._
import repositories.UserRepository

import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject()(repo: UserRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController {
  def index = Action.async { implicit request =>
    repo.list.map { user =>
      Ok(Json.toJson(user))
    }
  }

  def create = Action.async { implicit request =>
    val name = request.body

    repo.create("Phani", 29).map {user =>
      Ok(Json.toJson(user))
    }
  }
}
