package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.borer.given
import utest.*
import io.bullet.borer.{Encoder, Json}

object BorerSuite extends TestSuite:

  val tests: Tests = Tests {

    test("opaque alias encoding") {
      Json.encode(Temperature(15.0)).toUtf8String ==> "15.0"
    }

    test("opaque alias decoding") {
      Json.decode("15.0".getBytes).to[Temperature].valueEither ==> Right(Temperature(15.0))
      Json.decode("-15.0".getBytes).to[Temperature].valueEither.left.map(_.getMessage) ==>
        Left("Should be strictly positive (input position 0)")
    }

    test("transparent alias encoding") {
      Json.encode(15.0:Moisture).toUtf8String ==> "15.0"
    }

    test("transparent alias decoding") {
      Json.decode("15.0".getBytes).to[Moisture].valueEither ==> Right(15.0:Moisture)
      Json.decode("-15.0".getBytes).to[Moisture].valueEither.left.map(_.getMessage) ==>
        Left("Should be strictly positive (input position 0)")
    }
  }
