package io.github.iltotore.scalalint.constraint

class Constrained[T, C <: ConstraintAnchor](val value: T)(using behavior: Constraint[T, C]) {
  behavior.assert(value)
}
