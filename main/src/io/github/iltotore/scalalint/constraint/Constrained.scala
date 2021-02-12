package io.github.iltotore.scalalint.constraint

class Constrained[T, C <: ConstraintAnchor](val value: T)(using constraint: Constraint[T, C]) {
  constraint.assert(value).foreach((msg: String) => throw AssertionError(msg))
}
