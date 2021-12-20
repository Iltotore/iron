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

  "A Literal[V] constraint" should "compile if V" in {

    def dummy[V <: Boolean](x: Int / Literal[V]): Unit = ???

    "dummy[true](0)" should compile
    "dummy[false](1)" shouldNot compile
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

  "B" should "not verify Not[B]" in {
    val dummy: Boolean / Not[Dummy] = false
    "val foo: Boolean / Dummy = dummy" shouldNot compile
  }

  "Not[B]" should "not verify B" in {
    val dummy: Boolean / Dummy = true
    "val foo: Boolean / Not[Dummy] = dummy" shouldNot compile
  }

  "An Or[B, C] constraint" should "compile if the argument satisfies one of the two passed assertions" in {

    def dummy(x: Int / (StrictEqual[0] || StrictEqual[1])): Unit = ???

    "dummy(0)" should compile
    "dummy(1)" should compile
    "dummy(2)" shouldNot compile
  }

  "Both A and B" should "verify Or[A, B]" in {
    val a: Int / Even = -2
    val b: Int / Positive = 3
    "val foo: Int / (Even || Positive) = a" should compile
    "val foo: Int / (Even || Positive) = b" should compile
  }

  "An And[B, C] constraint" should "compile if the argument satisfies both B and C" in {

    def dummy(x: Int / (Positive && Even)): Unit = ???

    "dummy(2)" should compile
    "dummy(3)" shouldNot compile
    "dummy(-2)" shouldNot compile
  }

  "And[A, B]" should "verify both A and B" in {

    val dummy: Int / (Positive && Even) = 4
    "val a: Int / Positive = dummy" should compile
    "val b: Int / Even = dummy" should compile
  }

}