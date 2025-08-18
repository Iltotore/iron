package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.numeric.Positive
import io.github.iltotore.iron.upickle.given
import io.github.iltotore.iron.*
import _root_.upickle.default.*

import scala.util.Try
import utest.*

object uPickleSuite extends TestSuite:

  import scala.runtime.stdLibPatches.Predef.summon

  val tests: Tests = Tests:

    test("reader"):
      test("ironType"):
        test("success") - assert(Try(read[Int :| Positive]("10")).isSuccess)
        test("failure") - assert(Try(read[Int :| Positive]("-10")).isFailure)
      test("newType"):
        test("success") - assert(read[Temperature]("36.6") == Temperature(36.6))
        test("failure") - assert(Try(read[Temperature]("-36.6")).isFailure)
      test("subType"):
        test("success") - assert(read[Altitude]("10") == Altitude(10))
        test("failure") - assert(Try(read[Altitude]("-10")).isFailure)

    test("writer"):
      test("ironType"):
        val p: Int :| Positive = 10
        test("success") - assert(write(p) == "10")
      test("newType"):
        test("success") - assert(write(Temperature(36.6)) == "36.6")
      test("subType"):
        test("success") - assert(write(Altitude(10)) == "10")

end uPickleSuite
