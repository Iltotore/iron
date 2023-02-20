package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.*

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

object any extends LowPriorityArbitrary:

  inline given [A, C1, C2](using C1 ==> C2, C2 ==> C1, Arbitrary[A :| C1]): Arbitrary[A :| C2] =
    summon[Arbitrary[A :| C1]].asInstanceOf

trait LowPriorityArbitrary:

  inline given[A: Arbitrary, C](using inline constraint: Constraint[A, C]): Arbitrary[A :| C] =
    Arbitrary(arbitrary.filter(constraint.test(_))).asInstanceOf