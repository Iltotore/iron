package io.github.iltotore.iron.test.main

import org.scalatest._, flatspec._, matchers._
import io.github.iltotore.iron._, constraint.{_, given}
import io.github.iltotore.iron.test.{*, given}

class RuntimeSpec extends UnitSpec {

  "An Equal[V] constraint" should "return Right if the argument equals to V" in {

    def dummy(x: Int / Equal[0]): Int / Equal[0] = x

    assert(dummy(0).isRight)
    assert(dummy(1).isLeft)
  }

  "A RuntimeOnly[B] constraint" should "be evaluated at runtime" in {

    def dummy(x: Int / RuntimeOnly[StrictEqual[0]]): Int / RuntimeOnly[StrictEqual[0]] = x

    assert(dummy(0).isRight)
    "dummy(1)" should compile
    assert(dummy(1).isLeft)
  }
}
