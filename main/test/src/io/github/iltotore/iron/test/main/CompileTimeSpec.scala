package io.github.iltotore.iron.test.main

import org.scalatest._, flatspec._, matchers._
import io.github.iltotore.iron._, constraint.{_, given}

import io.github.iltotore.iron.test.{_, given}

class CompileTimeSpec extends UnitSpec {

  "A compile-time constraint" should "compile if assertion is passed" in {

    def dummy(x: Boolean / Dummy): Unit = ???

    "dummy(true)" should compile
    "dummy(false)" shouldNot compile
  }

  "A compiletime-only constraint" should "abort compilation if unable to be evaluated at compile time" in {

    def dummy(x: Boolean / DummyCompileTime): Unit = ???

    "dummy(true)" should compile
    "dummy(false)" shouldNot compile
    """val test = true
      dummy(test)""" shouldNot compile
  }

  "A runtime-only constraint" should "be evaluated at runtime only" in {

    def dummy(x: Boolean / DummyRuntime): Unit = ???

    "dummy(true)" should compile
    "dummy(false)" should compile
  }

  "A StrictEqual[V] constraint" should "compile if the argument == V" in {

    def dummy(x: Int == 0): Unit = ???

    "dummy(0)" should compile
    "dummy(1)" shouldNot compile
  }

  "A Not[B] constraint" should "compile if the argument doesn't pass the reversed constraint" in {

    def dummy(x: Int \ 0): Unit = ???

    "dummy(1)" should compile
    "dummy(0)" shouldNot compile
  }

  "An Or[B, C] constraint" should "compile if the argument satisfies one of the two passed assertions" in {

    def dummy(x: Int / (StrictEqual[0] || StrictEqual[1])): Unit = ???

    "dummy(0)" should compile
    "dummy(1)" should compile
    "dummy(2)" shouldNot compile
  }

  "An And[B, C] constraint" should "compile if the argument satisfies both B and C" in {

    def dummy(x: Int / (Positive && Even)): Unit = ???

    "dummy(2)" should compile
    "dummy(3)" shouldNot compile
    "dummy(-2)" shouldNot compile
  }
}