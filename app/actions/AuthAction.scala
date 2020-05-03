package auth

import javax.inject.Inject
import play.api.http.HeaderNames
import play.api.mvc._
import repositories.UserRepository
import models.User
import scala.concurrent.{ExecutionContext, Future}

case class UserRequest[A](user: User, request: Request[A]) extends WrappedRequest[A](request)

// Our custom action implementation
class AuthAction @Inject()(bodyParser: BodyParsers.Default, userRepository: UserRepository)(implicit ec: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = bodyParser
  override protected def executionContext: ExecutionContext = ec

  // A regex for parsing the Authorization header value
  private val headerTokenRegex = """Token token=(.+?)""".r

  // Called when a request is invoked. We should validate the bearer token here
  // and allow the request to proceed if it is valid.
  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] =
    extractBearerToken(request).map { token =>
      userRepository.findByApiKey(token).flatMap {
        case Some(user) => {
          block(UserRequest(user, request))
        }    // token was valid - proceed!
        case None => Future.successful(Results.Unauthorized("Invalid API Key"))  // token was invalid - return 401
      }
    }.getOrElse(Future(Results.Unauthorized("Please pass API Key")))   // no token was sent - return 401

  // Helper for extracting the token value
  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.headers.get(HeaderNames.AUTHORIZATION) collect {
      case headerTokenRegex(token) => token
    }
}