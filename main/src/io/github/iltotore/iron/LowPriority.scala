package io.github.iltotore.iron

import scala.compiletime.summonInline

trait LowPriority:

  class DefaultConsequence[A, C1, C2, Impl <: Constraint[A, C2]](using Impl) extends Theorem[A, C1, C2]:

    override inline def test(value: A): Boolean =
      summonInline[Impl].test(value)

    override inline def message: String =
      summonInline[Impl].message

  transparent inline given [A, B1, B2, Impl <: Constraint[A, B2]](using inline constraint: Impl): Theorem[A, B1, B2] =
    new DefaultConsequence