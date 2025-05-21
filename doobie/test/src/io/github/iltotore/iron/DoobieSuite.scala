package io.github.iltotore.iron

import _root_.doobie.*
import _root_.doobie.implicits.given
import cats.implicits.{*, given}
import io.github.iltotore.iron.constraint.numeric.Positive
import io.github.iltotore.iron.doobie.given
import utest.*

object DoobieSuite extends TestSuite:
  val tests: Tests = Tests:

    test("ironType Doobie givens are resolved"):
      Get[Int :| Positive]
      Put[Int :| Positive]
      Meta[Int :| Positive]

    test("newType Doobie givens are resolved"):
      Get[Temperature]
      Put[Temperature]
      Meta[Temperature]
