package io.github.iltotore.iron.test.catsSupport

import cats.implicits.*, cats.data.*, cats.syntax.all.*
import cats.Parallel

import org.scalatest._, flatspec._, matchers._

import io.github.iltotore.iron._, constraint._, catsSupport.{_, given}
import io.github.iltotore.iron.test.{*, given}

class RuntimeSpec extends UnitSpec {

  "Accumulated constraints" should "accumulate errors when processed in parallel" in {

    case class Foo(a: Boolean, b: Boolean)

    def createFoo(a: Boolean ==> DummyRuntime, b: Boolean ==> DummyRuntime): AccumulatedRefined[Foo] = Parallel.parMap2(
      a.accumulated,
      b.accumulated
    )(Foo.apply)

    assert(createFoo(true, true).isRight)
    assert(createFoo(false, true).left.exists(_.size == 1))
    assert(createFoo(false, false).left.exists(_.size == 2))
  }
}
