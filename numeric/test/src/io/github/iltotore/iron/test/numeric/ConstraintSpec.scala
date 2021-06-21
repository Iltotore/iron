package io.github.iltotore.iron.test.numeric

import io.github.iltotore.iron._, constraint._, numeric.constraint._
import io.github.iltotore.iron.test.UnitSpec

class ConstraintSpec extends UnitSpec {

  "A Greater[V] constraint" should "compile if the argument is > V" in {

    def dummy(x: Int ==> Greater[0]): Unit = {}

    "dummy(1)" should compile
    "dummy(-1)" shouldNot compile
  }

  "A Lesser[V] constraint" should "compile if the argument is < V" in {

    def dummy(x: Int < 0): Unit = {}

    "dummy(-1)" should compile
    "dummy(1)" shouldNot compile
  }

  "A Divisible[V] constraint" should "compile if the argument is congruent to 0[V]" in {

    def dummy(x: Int % 2): Unit = {}

    "dummy(2)" should compile
    "dummy(1)" shouldNot compile
  }
}
