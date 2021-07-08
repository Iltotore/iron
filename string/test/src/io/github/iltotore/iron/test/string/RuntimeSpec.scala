package io.github.iltotore.iron.test.string

import io.github.iltotore.iron.*, constraint.*, string.constraint.*
import io.github.iltotore.iron.test.UnitSpec

class RuntimeSpec extends UnitSpec {

  "A LowerCase constraint" should "return Right if the argument is lower case" in {

    def dummy(x: String ==> LowerCase): String ==> LowerCase = x

    assert(dummy("abc").isRight)
    assert(dummy("ABC").isLeft)
  }

  "An UpperCase constraint" should "return Right if the argument is upper case" in {

    def dummy(x: String ==> UpperCase): String ==> UpperCase = x

    assert(dummy("ABC").isRight)
    assert(dummy("abc").isLeft)
  }

  "A Matcher[V] constraint" should "return Right if the argument matches the given V regex" in {

    def dummy(x: String ==> Match["^[a-z0-9]+"]): String ==> Match["^[a-z0-9]+"] = x

    assert(dummy("abc123").isRight)
    assert(dummy("abc").isRight)
    assert(dummy("123").isRight)
    assert(dummy(" ").isLeft)
    assert(dummy("$!#").isLeft)
  }
}
