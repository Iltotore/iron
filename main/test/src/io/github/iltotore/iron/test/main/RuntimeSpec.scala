package io.github.iltotore.iron.test.main

import org.scalatest._, flatspec._, matchers._
import io.github.iltotore.iron._, constraint.{_, given}
import io.github.iltotore.iron.test.{*, given}

class RuntimeSpec extends UnitSpec {

  "An Equal[V] constraint" should "return Right if the argument equals to V" in {

    def dummy(x: Int ==> Equal[0]): Int ==> Equal[0] = x

    assert(dummy(0).isRight)
    assert(dummy(1).isLeft)
  }

  "A Not[B] constraint" should "return Right if the argument doesn't pass the reversed constraint" in {

    def dummy(x: Int \ 0): Int \ 0 = x

    assert(dummy(0).isLeft)
    assert(dummy(1).isRight)
  }

  "An Or[B, C] constraint" should "return Right if the argument satisfies one of the two passed assertions" in {

    def dummy(x: Int ==> (Equal[0] || Equal[1])): Int ==> (Equal[0] || Equal[1]) = x

    assert(dummy(0).isRight)
    assert(dummy(1).isRight)
    assert(dummy(2).isLeft)
  }

  "An And[B, C] constraint" should "return Right if the argument satisfies both B and C" in {

    def dummy(x: Int ==> (Positive && Even)): Int ==> (Positive && Even) = x

    assert(dummy(2).isRight)
    assert(dummy(3).isLeft)
    assert(dummy(-2).isLeft)
  }

  "A RuntimeOnly[B] constraint" should "be evaluated at runtime" in {

    def dummy(x: Int ==> RuntimeOnly[StrictEqual[0]]): Int ==> RuntimeOnly[StrictEqual[0]] = x

    assert(dummy(0).isRight)
    "dummy(1)" should compile
    assert(dummy(1).isLeft)
  }
}
