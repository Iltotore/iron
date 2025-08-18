package io.github.iltotore.iron.formCats

import cats.effect.IO

import io.circe.*
import io.circe.generic.auto.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.*
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*

import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.*

type Username = DescribedAs[
  Alphanumeric & MinLength[3] & MaxLength[10],
  "Username should be alphanumeric and have a length between 3 and 10"
]

type Password = DescribedAs[
  Match["[A-Za-z].*[0-9]|[0-9].*[A-Za-z]"] & MinLength[6] & MaxLength[20],
  "Password must contain at least a letter, a digit and have a length between 6 and 20"
]

type Age = DescribedAs[Greater[0], "Age should be strictly positive"]

/**
 * A basic Account with a name, a password and an age.
 *
 * @param name an alphanumeric String with a length between 3 and 10.
 * @param password an String containing atleast a letter, a digit, with a length between 6 and 20.
 * @param age a strictly positive Int.
 */
case class Account(name: String :| Username, password: String :| Password, age: Int :| Age)

object Account:

  /**
   * Account to Response encoder for Http4s.
   */
  given EntityEncoder[IO, Account] = jsonEncoderOf[IO, Account]

  /**
   * Request to Account decoder for Http4s.
   */
  given EntityDecoder[IO, Account] = accumulatingJsonOf[IO, Account]
