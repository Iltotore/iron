package io.github.iltotore.iron

import io.github.iltotore.iron.playJson.given
import play.api.libs.json.{Reads, Writes}
import io.github.iltotore.iron.constraint.numeric.Positive
import utest.*

object PlayJsonSuite extends TestSuite:

  val tests: Tests = Tests:
    test("PlayJson instances are resolved for Double iron types"):
      summon[Reads[Double :| Positive]]
      summon[Writes[Double :| Positive]]

    test("PlayJson instances are resolved for new types"):
      summon[Reads[Temperature]]
      summon[Writes[Temperature]]

    test("PlayJson instances are resolved for new subtypes"):
      summon[Reads[Altitude]]
      summon[Writes[Altitude]]
