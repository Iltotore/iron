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
        summon[Reader[Temperature]]

    test("writer"):
      test("ironType"):
        val p: Int :| Positive = 10
        test("success") - assert(write(p) == "10")
      test("newType"):
        summon[Writer[Temperature]]

end uPickleSuite
