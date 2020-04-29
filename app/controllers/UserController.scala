package controllers

import javax.inject.{Inject, Singleton}
import models.User
import org.mindrot.jbcrypt.BCrypt
import play.api.mvc.{BaseController, ControllerComponents, Result}
import play.api.libs.json._
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class UserController @Inject()(repo: UserRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController {
  def index = Action.async { implicit request =>
    repo.list.map { user =>
      Ok(Json.toJson(user))
    }
  }

  def create = Action.async { implicit request =>
    try {
      val name: String = request.body.asJson.get("name").as[String]
      val email: String = request.body.asJson.get("email").as[String]
      val password: String = request.body.asJson.get("password").as[String]
      val age: Int = request.body.asJson.get("age").as[Int]
      val weight: Int = request.body.asJson.get("weight").as[Int]
      val gender: String = request.body.asJson.get("weight").as[String]

      repo.findByEmail(email).flatMap[Result] {
        case Some(_: User) => Future(NotAcceptable("User already exists"))
        case None => repo.create(name, email, age, weight, gender, password).map { user =>
                       Ok(Json.toJson(user))
                     }
      }
    } catch {
      case _ : Throwable => Future(BadRequest("All fields are not passed"))
    }
  }

  def login = Action.async { implicit request =>
    val email = request.body.asJson.get("email").as[String]
    val password = request.body.asJson.get("password").as[String]

    repo.findByEmail(email).map({
      case Some(user: User) =>   {
        if (user.isValidPassword(password)) Ok(Json.toJson(user))
        else NotFound("User not found")
      }
      case None =>  NotFound("User not found")
    })
  }
}
