package io.github.iltotore.iron.test

import org.scalatest._
import flatspec._
import io.github.iltotore.iron._, constraint._
import matchers._

class ConstraintSpec extends UnitSpec {

  "A compile-time constraint" should "compile if assertion is passed" in {
    "valueToConstrained[Boolean, Dummy](true)" should compile
    "valueToConstrained[Boolean, Dummy](false)" shouldNot compile
  }

  "An Equal[V] constraint" should "compile if the argument == V" in {

    def dummy(x: Int == 0): Unit = {}

    "dummy(0)" should compile
    "dummy(1)" shouldNot compile
  }
}