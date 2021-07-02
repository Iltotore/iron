package io.github.iltotore.iron.test

import org.scalatest._, flatspec._, matchers._
import io.github.iltotore.iron._, constraint.{_, given}

class CompileTimeSpec extends UnitSpec {

  "A compile-time constraint" should "compile if assertion is passed" in {
    "valueToConstrained[Boolean, Dummy](true)" should compile
    "valueToConstrained[Boolean, Dummy](false)" shouldNot compile
  }

  "A StrictEqual[V] constraint" should "compile if the argument == V" in {

    def dummy(x: Int == 0): Unit = {}

    "dummy(0)" should compile
    "dummy(1)" shouldNot compile
  }
}