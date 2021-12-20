package io.github.iltotore.iron.constraint

import scala.compiletime.summonInline

trait LowPriorityConsequence {

  class DefaultConsequence[A, B1, B2, C <: Constraint[A, B2]](using C) extends Consequence[A, B1, B2] {

    override inline def assert(value: A): Boolean = summonInline[C].assert(value)

    override inline def getMessage(value: A): String = summonInline[C].getMessage(value)
  }

  inline given defaultConsequence[A, B1, B2, Cons <: Constraint[A, B2]](using inline constraint: Cons): DefaultConsequence[A, B1, B2, Cons] = new DefaultConsequence

}
