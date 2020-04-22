package controllers

import javax.inject.{Inject, Singleton}
import models.User
import org.mindrot.jbcrypt.BCrypt
import play.api.mvc.{BaseController, ControllerComponents}
import play.api.libs.json._
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(repo: UserRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController {
  def index = Action.async { implicit request =>
    repo.list.map { user =>
      Ok(Json.toJson(user))
    }
  }

  def create = Action.async { implicit request =>
    try {
      val name: String = request.body.asJson.get("name").toString
      val email: String = request.body.asJson.get("email").toString
      val age: Int = request.body.asJson.get("age").toString.toInt
      val weight: Int = request.body.asJson.get("weight").toString.toInt
      val password: String = request.body.asJson.get("password").toString

      repo.create(name, email, age, weight, password).map { user =>
        Ok(Json.toJson(user))
      }
    } catch {
      case _ => Future(BadRequest("All fields are not passed"))
    }
  }

  def login = Action.async { implicit request =>
    val email = request.body.asJson.get("email").toString()
    val password = request.body.asJson.get("password").toString()

    repo.findByEmail(email).map( user => {
      if (user.isValidPassword(password)) Ok(Json.toJson(user))
      else NotFound
    });
  }
}
