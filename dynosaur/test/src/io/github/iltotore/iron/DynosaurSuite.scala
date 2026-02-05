package io.github.iltotore.iron

import _root_.dynosaur.Schema
import dynosaur.given
import cats.implicits.{*, given}
import io.github.iltotore.iron.constraint.numeric.Positive
import utest.*

object DynosaurSuite extends TestSuite:

  val tests: Tests = Tests:

    test("ironType Dynosaur givens are resolved"):
      Schema[Int :| Positive]

    test("newType Dynosaur givens are resolved"):
      Schema[Temperature]

    test("newSubType Dynosaur givens are resolved"):
      Schema[Altitude]
