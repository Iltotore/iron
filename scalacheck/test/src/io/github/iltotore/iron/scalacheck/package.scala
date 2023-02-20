package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.{:|, Constraint}
import org.scalacheck.{Arbitrary, Prop}

inline def testGen[A, C](using inline arb: Arbitrary[A :| C], inline constraint: Constraint[A, C]): Unit =
  Prop.forAll(arb.arbitrary)(constraint.test(_)).check()
