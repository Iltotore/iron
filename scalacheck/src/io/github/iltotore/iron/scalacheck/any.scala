package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.any.StrictEqual
import org.scalacheck.*
import org.scalacheck.Arbitrary.arbitrary

import scala.compiletime.constValue

object any extends LowPriorityArbitrary:

  inline given [A, C1, C2](using C1 ==> C2, C2 ==> C1, Arbitrary[A :| C1]): Arbitrary[A :| C2] =
    summon[Arbitrary[A :| C1]].asInstanceOf

  inline given [A, V <: A]: Arbitrary[A :| StrictEqual[V]] = Arbitrary(Gen.oneOf(Seq(constValue[V]))).asInstanceOf

trait LowPriorityArbitrary:

  inline given[A: Arbitrary, C](using inline constraint: Constraint[A, C]): Arbitrary[A :| C] =
    Arbitrary(arbitrary.filter(constraint.test(_))).asInstanceOf