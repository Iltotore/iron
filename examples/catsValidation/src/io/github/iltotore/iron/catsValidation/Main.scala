package io.github.iltotore.iron.catsValidation

import cats.data.EitherNel
import cats.effect.{IO, IOApp}
import cats.syntax.all.*
import io.github.iltotore.iron.{given, *}
import io.github.iltotore.iron.cats.*
import io.github.iltotore.iron.constraint.numeric.given
import io.github.iltotore.iron.constraint.numeric.Greater
import io.github.iltotore.iron.constraint.string.given
import io.github.iltotore.iron.constraint.string.Contain

object Main extends IOApp.Simple:

  case class Person(
      name: String :| Contain["a"],
      surname: String :| Contain["z"],
      age: Int :| Greater[0]
  )

  def program(
      name: String,
      surname: String,
      age: Int
  ): EitherNel[String, Person] =
    (
      name.refineNel[Contain["a"]],
      surname.refineNel[Contain["z"]],
      age.refineNel[Greater[0]]
    ).parMapN(Person.apply)

  val run: IO[Unit] =
    program("foo", "bar", 0).fold(e => IO.println(s"ERROR: ${e.show}"), IO.println)
