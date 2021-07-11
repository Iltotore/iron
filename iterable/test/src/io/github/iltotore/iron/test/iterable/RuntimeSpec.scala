package io.github.iltotore.iron.test.iterable

import io.github.iltotore.iron.*, constraint.*, iterable.constraint.*
import io.github.iltotore.iron.test.UnitSpec

class RuntimeSpec extends UnitSpec {

  "A MinSize[V] constraint" should "return Right if the argument has a size greater than V" in {

    def dummy(x: Iterable[Int] ==> MinSize[3]): Iterable[Int] ==> MinSize[3] = x

    assert(dummy(0 until 3).isRight)
    assert(dummy(0 until 5).isRight)
    assert(dummy(0 until 2).isLeft)
  }
}
