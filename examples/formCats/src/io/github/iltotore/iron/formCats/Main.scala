package io.github.iltotore.iron.formCats

import cats.effect.{ExitCode, IO, IOApp}

import com.comcast.ip4s.*

import io.circe.ParsingFailure

import org.http4s.ember.server.EmberServerBuilder
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*

object Main extends IOApp:

  override def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"8080")
      .withHttpApp(HttpServer.service.orNotFound)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)