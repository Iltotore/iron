package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.{:|, Constraint}
import org.scalacheck.Test.Parameters
import org.scalacheck.{Arbitrary, Prop, Test}
import utest.*

inline def testGen[A, C](using inline arb: Arbitrary[A :| C], inline constraint: Constraint[A, C]): Unit =
  def getTestValues(args: List[Prop.Arg[Any]]): List[TestValue] =
    args.zipWithIndex.map((arg, i) => TestValue(if arg.label.isBlank then s"value$i" else arg.label, "T", arg.arg))

  val result = Test.check(Prop.forAll(arb.arbitrary)(constraint.test(_)))(p => p)
  result.status match
    case Test.Passed | Test.Proved(_) => assert(result.discarded == 0)
    case Test.Failed(args, _) =>
      throw AssertionError(s"Some constrained values failed for ${constraint.message}", getTestValues(args))
    case Test.Exhausted => throw new java.lang.AssertionError("Exhausted")
    case Test.PropException(args, e, _) =>
      throw AssertionError(s"An error occurred for ${constraint.message}", getTestValues(args), e)
