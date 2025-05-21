package io.github.iltotore.iron

import io.github.iltotore.iron.circe.given
import io.circe.*
import io.github.iltotore.iron.constraint.numeric.Positive
import utest.*

object CirceSuite extends TestSuite:

  val tests: Tests = Tests:
    test("Circe instances are resolved for Double iron types"):
      Encoder[Double :| Positive]
      Decoder[Double :| Positive]

    test("Circe instances are resolved for new types"):
      Encoder[Temperature]
      Decoder[Temperature]
