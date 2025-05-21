package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.numeric.Positive
import io.github.iltotore.iron.zioJson.{*, given}
import zio.json.{JsonDecoder, JsonEncoder}
import utest.*

object ZIOJsonSuite extends TestSuite:
  val tests: Tests = Tests:

    test("ironType givens are properly resolved"):
      summon[JsonDecoder[Double :| Positive]]
      summon[JsonEncoder[Double :| Positive]]

    test("newType givens are properly resolved"):
      summon[JsonDecoder[Temperature]]
      summon[JsonEncoder[Temperature]]
