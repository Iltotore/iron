import zio.http.*
import zio.http.model.{Method, Status}
import zio.json.*

import Account.given

object HttpServer:

  /**
   * Create a "Bad request to the API".
   *
   * @param message the error message to "throw".
   * @return a program returning a properly formatted "bad request" [[Response]].
   */
  def badApiRequest(message: String): Response =
    Response
      .json(Map("message" -> message).toJson)
      .setStatus(Status.BadRequest)

  /**
   * The main logic of our mini web server.
   * Register the given user passed as a `POST` [[Request]] to `/register` then return it as a `Response`
   */
  val app: HttpApp[Any, Throwable] =
    Http.collectZIO[Request] {
      case req @ Method.POST -> !! / "register" =>
        req.body.asString
          .map(_.fromJson[Account])
          .map(_.fold(badApiRequest, account => Response.json(account.toJson)))
    }
