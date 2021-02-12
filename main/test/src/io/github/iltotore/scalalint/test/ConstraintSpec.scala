package io.github.iltotore.scalalint.test

import io.github.iltotore.scalalint.constraint._, numeric._
import org.scalatest._, flatspec._, matchers._

class ConstraintSpec extends UnitSpec {

  "A runtime constraint" should "return an error message if the value doesn't match the internal assertion" in {
    Positive.assert(1).isEmpty shouldBe true
    Positive.assert(1).isEmpty shouldBe true
    Positive.assert(-1).isEmpty shouldBe false
  }

  it should "include effect of mixed-with constraints" in {
    val positiveAndNotNull = composedConstraint[Double, Positive, NotNull]
    positiveAndNotNull.assert(1).isEmpty shouldBe true
    positiveAndNotNull.assert(-1).isEmpty shouldBe false
    positiveAndNotNull.assert(0).isEmpty shouldBe false
  }
  
  "A compile time constraint" should "return an error message if the value doesn't match the internal condition" in {
    NotNull.CompileTime.assertCompileTime(0).isEmpty shouldBe false
    NotNull.CompileTime.assertCompileTime(1).isEmpty shouldBe true
  }
  
  it should "prevent compilation when calling assertInlined if an assertion is not passed" in {
    "NotNull.CompileTime.assertInlined(0)" shouldNot compile
    "NotNull.CompileTime.assertInlined(1)" should compile
  }
}