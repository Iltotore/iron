package io.github.iltotore.iron

import io.bullet.borer.{Encoder, Json}
import io.github.iltotore.iron.borer.given
import utest.*

object BorerSuite extends TestSuite:

  val tests: Tests = Tests:

    test("encoding"):
      Json.encode(Temperature(15.0)).toUtf8String ==> "15.0"

    test("decoding"):
      Json.decode("15.0".getBytes).to[Temperature].valueEither ==> Right(Temperature(15.0))
      Json.decode("-15.0".getBytes).to[Temperature].valueEither.left.map(_.getMessage) ==>
        Left("Should be strictly positive (input position 0)")
