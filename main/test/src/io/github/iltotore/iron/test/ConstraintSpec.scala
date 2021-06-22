package io.github.iltotore.iron.test

import org.scalatest._
import flatspec._
import io.github.iltotore.iron.constraint.valueToConstrained
import matchers._

class ConstraintSpec extends UnitSpec {

  "A compile-time constraint" should "compile if assertion is passed" in {
    "valueToConstrained[Boolean, Dummy](true)" should compile
    "valueToConstrained[Boolean, Dummy](false)" shouldNot compile
  }
}