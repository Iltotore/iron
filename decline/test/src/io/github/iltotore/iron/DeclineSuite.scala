package io.github.iltotore.iron

import _root_.com.monovore.decline.Argument
import _root_.cats.data.Validated.Valid
import io.github.iltotore.iron.decline.given
import io.github.iltotore.iron.constraint.numeric.Positive
import utest.*

object DeclineSuite extends TestSuite:
  val tests: Tests = Tests {

    test("Argument") {
      test("ironType") {
        test("success") - assert(summon[Argument[Int :| Positive]].read("5") == Valid(5))
        test("failure") - assert(summon[Argument[Int :| Positive]].read("-5").isInvalid)
      }

      test("newType") {
        test("success") - assert(summon[Argument[Temperature]].read("5") == Valid(5))
        test("failure") - assert(summon[Argument[Temperature]].read("-5").isInvalid)
      }
    }
  }
