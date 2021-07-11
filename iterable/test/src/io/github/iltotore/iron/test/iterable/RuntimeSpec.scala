package io.github.iltotore.iron.test.iterable

import io.github.iltotore.iron.*, constraint.*, iterable.constraint.*
import io.github.iltotore.iron.test.UnitSpec

class RuntimeSpec extends UnitSpec {

  "A MinSize[V] constraint" should "return Right if the argument has a size greater or equal to V" in {

    def dummy(x: Iterable[Int] ==> MinSize[3]): Iterable[Int] ==> MinSize[3] = x

    assert(dummy(0 until 3).isRight)
    assert(dummy(0 until 5).isRight)
    assert(dummy(0 until 2).isLeft)
  }

  "A MaxSize[V] constraint" should "return Right if the argument has a size lesser or equal to V" in {

    def dummy(x: Iterable[Int] ==> MaxSize[3]): Iterable[Int] ==> MaxSize[3] = x

    assert(dummy(0 until 3).isRight)
    assert(dummy(0 until 2).isRight)
    assert(dummy(0 until 5).isLeft)
  }

  "A Size[V] constraint" should "return Right if the argument has a size of V" in {

    def dummy(x: Iterable[Int] ==> Size[3]): Iterable[Int] ==> Size[3] = x

    assert(dummy(0 until 3).isRight)
    assert(dummy(0 until 2).isLeft)
    assert(dummy(0 until 5).isLeft)
  }

  "A Contains[V] constraint" should "return Right if the argument contains V" in {

    def dummy(x: Iterable[Int] ==> Contains[2]): Iterable[Int] ==> Contains[2] = x

    assert(dummy(Seq(1, 2, 3)).isRight)
    assert(dummy(Seq(1, 3, 4)).isLeft)
  }
}
