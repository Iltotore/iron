package io.github.iltotore.iron.formCats

import cats.effect.{IO, Resource, ResourceApp}
import cats.syntax.functor.*

import com.comcast.ip4s.*

import io.circe.ParsingFailure

import org.http4s.ember.server.EmberServerBuilder
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*

object Main extends ResourceApp.Forever:

  override def run(args: List[String]): Resource[IO, Unit] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"8080")
      .withHttpApp(HttpServer.service.orNotFound)
      .build
      .void
