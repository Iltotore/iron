package io.github.iltotore.iron.borerSerialization

import io.bullet.borer.{Borer, Json}
import io.github.iltotore.iron.*

@main def main: Unit =

  val account = Account(
    name = "matt",
    password = "bar123",
    age = 42
  )

  val okValidEncoding = """{"name":"matt","password":"bar123","age":42}"""
  val invalidEncoding = """{"name":"matt","password":"bar","age":42}"""

  val encoding = Json.encode(account).toUtf8String
  println(encoding)
  assert(encoding == okValidEncoding)

  val decoding = Json.decode(okValidEncoding.getBytes).to[Account].valueEither
  println(decoding)
  assert(decoding == Right(account))

  val decoding2 = Json.decode(invalidEncoding.getBytes).to[Account].valueEither
  decoding2 match
    case Left(e: Borer.Error.ValidationFailure[?]) =>
      // "Password must contain at least a letter, a digit and have a length between 6 and 20 (input position 26)"
      println(e.getMessage)
    case _ => throw new IllegalStateException
