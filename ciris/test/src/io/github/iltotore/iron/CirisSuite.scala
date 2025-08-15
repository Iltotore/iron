package io.github.iltotore.iron

import _root_.ciris.ConfigDecoder
import io.github.iltotore.iron.ciris.given
import io.github.iltotore.iron.constraint.numeric.Positive
import utest.*

object CirisSuite extends TestSuite:
  val tests: Tests = Tests:

    test("decoder"):
      test("ironType"):
        test("success") - assert(summon[ConfigDecoder[String, Int :| Positive]].decode(None, "5") == Right(5))
        test("failure") - assert(summon[ConfigDecoder[String, Int :| Positive]].decode(None, "-5").isLeft)

      test("newType"):
        test("success") - assert(summon[ConfigDecoder[String, Temperature]].decode(None, "5") == Right(Temperature(5)))
        test("failure") - assert(summon[ConfigDecoder[String, Temperature]].decode(None, "-5").isLeft)

      test("newSubType"):
        test("success") - assert(summon[ConfigDecoder[String, Altitude]].decode(None, "5") == Right(Altitude(5)))
        test("failure") - assert(summon[ConfigDecoder[String, Altitude]].decode(None, "-5").isLeft)
