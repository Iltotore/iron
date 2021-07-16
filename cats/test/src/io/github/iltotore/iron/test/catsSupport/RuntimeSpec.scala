package io.github.iltotore.iron.test.catsSupport


import cats.implicits.*, cats.syntax.apply.*

import org.scalatest._, flatspec._, matchers._

import io.github.iltotore.iron._, constraint._, catsSupport.{_, given}
import io.github.iltotore.iron.test.{*, given}

class RuntimeSpec extends UnitSpec {

  "Accumulated constraints" should "accumulate errors when processed in parallel" in {

    case class Foo(a: Boolean, b: Boolean)

    def createFoo(a: Boolean ==> DummyRuntime, b: Boolean ==> DummyRuntime): RefinedNec[Foo] = (
      a.validatedNec,
      b.validatedNec
    ).mapN(Foo.apply)

    assert(createFoo(true, true).isValid)
    assert(createFoo(false, true).swap.exists(_.size == 1))
    assert(createFoo(false, false).swap.exists(_.size == 2))
  }
}