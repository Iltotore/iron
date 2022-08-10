package io.github.iltotore.iron

import scala.compiletime.{constValue, summonInline}

object constraints extends LowPriority:

  final class DescribedAs[C, V <: String]

  class DescribedAsConstraint[A, C, Impl <: Constraint[A, C], V <: String](using Impl)
      extends Constraint[A, DescribedAs[C, V]]:

    override inline def test(value: A): Boolean = summonInline[Impl].test(value)

    override inline def message: String = constValue[V]

  transparent inline given [A, C, Impl <: Constraint[A, C], V <: String](using inline constraint: Impl): Constraint[A, DescribedAs[C, V]] = new DescribedAsConstraint

  final class Not[B]

  given[A, B, C <: Constraint[A, B]] (using constraint: C): Constraint[A, Not[B]] with

    override inline def test(value: A): Boolean = !constraint.test(value)

    override inline def message: String = "not"
