package io.github.iltotore.iron.test.string

import io.github.iltotore.iron.*, constraint.*, string.constraint.*
import io.github.iltotore.iron.test.UnitSpec

class RuntimeSpec extends UnitSpec {


  "A LowerCase constraint" should "return Right if the argument is lower case" in {

    def dummy(x: String ==> LowerCase): String ==> LowerCase = x

    assert(dummy("abc").isRight)
    assert(dummy("ABC").isLeft)
  }
}
