package io.github.iltotore.iron.formCats

import cats.effect.IO
import cats.data.{NonEmptyList, ValidatedNel}

import io.circe.*

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.circe.CirceEntityEncoder.* //To Encode a Map as Json in Response
import org.http4s.circe.DecodingFailures

import Account.given //Account entity codec

object HttpServer:

  /**
    * Register the given [[Account]].
    *
    * @param account the [[Account]] to register.
    * @return a program returning an "Ok" [[Response]].
    */
  def register(account: Account): IO[Response[IO]] =
    IO.println(s"Registered $account") *> Ok(account)

  /**
    * Create a "Bad request to the API".
    *
    * @param messages the error messages to "throw".
    * @return a program returning a properly formatted "bad request" [[Response]].
    */
  def badApiRequest(messages: NonEmptyList[String]): IO[Response[IO]] =
    BadRequest(Map("messages" -> messages))

  /**
    * Handle the given error. Print it and return the appropriated
    *
    * @param error the error to handle.
    * @return a program returning a "bad request" or an "internal server error" [[Response]].
    */
  def handleError(error: Throwable): IO[Response[IO]] =
    val response = 
      error match
        case InvalidMessageBodyFailure(defaultMessage, cause) =>
          cause match
            case Some(DecodingFailure(message, _)) => badApiRequest(NonEmptyList.one(message))
            case Some(DecodingFailures(failures)) => badApiRequest(failures.map(_.message))
            case _ => badApiRequest(NonEmptyList.one(defaultMessage))
        case MalformedMessageBodyFailure(defaultMessage, cause) =>
          cause match
            case Some(ParsingFailure(message, _)) => badApiRequest(NonEmptyList.one(message))
            case _ => badApiRequest(NonEmptyList.one(defaultMessage))
        case _=> InternalServerError()

    IO(error.printStackTrace()) *> response


  /**
    * The main logic of our mini web server.
    * Register the given user passed as a `POST` [[Request]] to `/register` then return it as a `Response`
    */
  val service = HttpRoutes.of[IO] { //Can be replaced with colon + indentation in Scala 3.3
    case request@POST -> Root / "register" =>
      val routine =
        for
          account <- request.as[Account]
          response <- register(account)
        yield response

      routine.handleErrorWith(handleError)


    case unknown => NotFound()
  }
