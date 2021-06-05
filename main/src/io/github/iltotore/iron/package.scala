package io.github.iltotore

import io.github.iltotore.iron.constraint.Constraint

import scala.compiletime.constValue


package object iron {

  opaque type Constrained[A, B] <: A = A
  type ==>[A, B] = Constrained[A, B]

  object Constrained {
    def apply[A, B](value: A)(using contraint: Constraint[A, B]): Constrained[A, B] = {
      contraint.runtimeAssert(value)
      value
    }

    def unchecked[A, B](value: A): Constrained[A, B] = value
  }
}