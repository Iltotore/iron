package io.github.iltotore.iron.test

import org.scalatest._, flatspec._, matchers._
import io.github.iltotore.iron._, constraint.{_, given}

class CompileTimeSpec extends UnitSpec {

  "A compile-time constraint" should "compile if assertion is passed" in {

    def dummy(x: Boolean ==> Dummy): Unit = {}

    "dummy(true)" should compile
    "dummy(false)" shouldNot compile
  }

  "A compile-only constraint" should "abort compilation if unable to be evaluated at compile time" in {

    def dummy(x: Boolean ==> DummyCompileTime): Unit = {}

    "dummy(true)" should compile
    "dummy(false)" shouldNot compile
    """val test = true
      dummy(test)""" shouldNot compile
  }

  "A StrictEqual[V] constraint" should "compile if the argument == V" in {

    def dummy(x: Int == 0): Unit = {}

    "dummy(0)" should compile
    "dummy(1)" shouldNot compile
  }
}