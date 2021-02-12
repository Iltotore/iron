package io.github.iltotore.scalalint.test

import io.github.iltotore.scalalint.constraint._, numeric._
import org.scalatest._, flatspec._, matchers._

class ConstraintSpec extends UnitSpec {

  "A runtime constraint" should "return an error message if the value doesn't match the internal condition" in {
    Positive.assert(1).isEmpty shouldBe true
    Positive.assert(1).isEmpty shouldBe true
    Positive.assert(-1).isEmpty shouldBe false
  }

  "A composed constraint" should "include effect of components" in {
    val positiveAndNotNull = composedConstraint[Double, Positive, NotNull]
    positiveAndNotNull.assert(1).isEmpty shouldBe true
    positiveAndNotNull.assert(-1).isEmpty shouldBe false
    positiveAndNotNull.assert(0).isEmpty shouldBe false
  }
}