import zio.http.*
import zio.json.*

import Account.given
import zio.ZIO

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
      .status(Status.BadRequest)

  /**
   * The main logic of our mini web server.
   * Register the given user passed as a `POST` [[Request]] to `/register` then return it as a `Response`
   */
  val routes: Routes[Any, Response] =
    Routes(
      Method.POST / "register" -> handler: (req: Request) =>
        req
          .body
          .asString.mapError(_ => badApiRequest("Body is not a String"))
          .flatMap(json => ZIO.fromEither(json.fromJson[Account]).mapError(badApiRequest))
          .map(account => Response.json(account.toJson))
    )
